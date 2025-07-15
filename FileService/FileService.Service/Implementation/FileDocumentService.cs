using FileService.Core.ApiModels;
using FileService.Core.Enums;
using FileService.Core.Exceptions;
using FileService.Core.Helpers;
using FileService.DataAccess.Interfaces;
using FileService.DataAccess.Models;
using FileService.Service.ApiModels.FileDocumentModels;
using FileService.Service.Dtos.FileDocumentDtos;
using FileService.Service.Interfaces;
using System;
using System.Security.AccessControl;

namespace FileService.Service.Implementation
{
    public class FileDocumentService : BaseService, IFileDocumentService
    {
        private readonly IRepository<FileDocument> _fileDocumentRepository;
        private readonly UserContext _userContext;
        private readonly IFfmpegService _ffmpegService;
        private readonly IMinioService _minioService;


        public FileDocumentService(
            AppSettings appSettings,
            UserContext userContext,
            IFfmpegService ffmpegService,
            IMinioService minioService,
            IRepository<FileDocument> fileDocumentRepository)
             : base(appSettings, userContext)
        {
            _fileDocumentRepository = fileDocumentRepository;
            _userContext = userContext;
            _ffmpegService = ffmpegService;
            _minioService = minioService;
        }

        private void ValidatePagingModel(FileDocumentDto fileDocumentDto)
        {
            if (fileDocumentDto.PageIndex < 1)
                throw new ErrorException(Core.Enums.StatusCodeEnum.PageIndexInvalid);
            if (fileDocumentDto.PageSize < 1)
                throw new ErrorException(Core.Enums.StatusCodeEnum.PageSizeInvalid);
        }
        public async Task<IEnumerable<FileDocumentModel>> GetViewModelsAsync(FileDocumentDto fileDocumentDto)
        {
            // Validate PageIndex and PageSize
            ValidatePagingModel(fileDocumentDto);

            var items = await _fileDocumentRepository.FilterAsync(x => !x.IsDeleted);

            return items.Select(file => new FileDocumentModel
            {
                Id = file.Id,
                FileName = file.FileName,
                FileType = file.FileType,
                Url = file.Url,
                Size = file.Size,
                Checksum = file.Checksum,
                Category = file.Category,
                IsActive = file.IsActive,
                Tags = file.Tags,
                ViewCount = file.ViewCount,
                Rating = file.Rating,
                Description = file.Description,
                OrgId = file.OrgId,
                ThumbnailUrl = file.ThumbnailUrl,
                HlsUrl = file.HlsUrl,
                Duration = file.Duration,
                IsLectureVideo = file.IsLectureVideo,
                IsTextbook = file.IsTextbook,
                TranscodingStatus = file.TranscodingStatus,
                AssociatedResourceIds = file.AssociatedResourceIds
            });
        }

        public async Task<FileDocumentModel> GetFileDetailById(Guid id)
        {
            var file = await _fileDocumentRepository.GetByIdAsync(id);
            if (file == null || file.IsRemoved)
                throw new ErrorException(StatusCodeEnum.A02);

            // Tạo presigned URL từ MinIO
            var presignedUrl = await _minioService.GetFileAsync(file.Url.TrimStart('/')); // Loại bỏ '/' đầu nếu có

            return new FileDocumentModel
            {
                Id = file.Id,
                FileName = file.FileName,
                FileType = file.FileType,
                Url = presignedUrl,
                Size = file.Size,
                Checksum = file.Checksum,
                Category = file.Category,
                IsActive = file.IsActive,
                Tags = file.Tags,
                ViewCount = file.ViewCount,
                Rating = file.Rating,
                Description = file.Description,
                OrgId = file.OrgId,
                ThumbnailUrl = file.ThumbnailUrl,
                Duration = file.Duration,
                IsLectureVideo = file.IsLectureVideo,
                IsTextbook = file.IsTextbook,
                TranscodingStatus = file.TranscodingStatus,
                AssociatedResourceIds = file.AssociatedResourceIds,
                HlsUrl = file.HlsUrl
            };
        }

        public async Task AddFileAsync(AddFileDocumentDto dto)
        {
            if (dto.File == null || dto.File.Length == 0)
                throw new ErrorException(StatusCodeEnum.A01);

            if (string.IsNullOrWhiteSpace(dto.File.FileName))
                throw new ErrorException(StatusCodeEnum.A04);

            if (dto.File.FileName.Length > 255)
                throw new ErrorException(StatusCodeEnum.A05);

            if (dto.Tags == null || !dto.Tags.Any())
                throw new ErrorException(StatusCodeEnum.A06);

            if (string.IsNullOrWhiteSpace(dto.Category.ToString()))
                throw new ErrorException(StatusCodeEnum.A07);

            var fileId = Guid.NewGuid();

            var ext = Path.GetExtension(dto.File.FileName);
            var fileName = $"files/{fileId}{ext}";

            //tạo và tải file tạm
            var tempFilePath = Path.GetTempFileName();
            await using (var stream = new FileStream(tempFilePath, FileMode.Create))
            {
                await dto.File.CopyToAsync(stream);
            }

            //upload lên MinIO

            await _minioService.UploadFileAsync(fileName, tempFilePath, dto.File.ContentType);

            //xóa file tạm với kiểm tra
            if (File.Exists(tempFilePath))
            {
                try
                {
                    File.Delete(tempFilePath);
                    Console.WriteLine($"Deleted temp file: {tempFilePath}");
                }
                catch (IOException ex)
                {
                    Console.WriteLine($"Failed to delete file {tempFilePath}: {ex.Message}");
                    throw;
                }
            }

            var file = new FileDocument
            {
                Id = fileId,
                FileName = dto.File.FileName,
                FileType = dto.File.ContentType,
                Size = dto.File.Length,
                Url = fileName,
                Description = dto.Description,
                Category = dto.Category,
                Tags = dto.Tags ?? new List<string>(),
                IsActive = true,
                CreatedAt = DateTime.UtcNow,
                CreatedBy = _userContext.UserId,
                OrgId = string.IsNullOrEmpty(dto.OrgId) ? null : dto.OrgId,
                IsLectureVideo = dto.IsLectureVideo,
                IsTextbook = dto.IsTextbook
            };

            var checksum = await FileHelper.ComputeChecksumAsync(dto.File.OpenReadStream());
            var exists = await _fileDocumentRepository.ExistsAsync(f => f.Checksum == checksum && !f.IsRemoved);
            if (exists)
                throw new ErrorException(StatusCodeEnum.A03);

            if (dto.IsLectureVideo)
            {
                try
                {
                    var result = await _ffmpegService.ProcessVideoAsync(dto.File);
                    file.ThumbnailUrl = result.ThumbnailUrl;
                    file.TranscodingStatus = result.Status;
                    file.Duration = result.Duration;
                    file.ViewCount = 0;
                    file.Rating = 0;
                    file.HlsUrl = result.HlsUrl; 
                }
                catch (Exception)
                {
                    file.TranscodingStatus = "failed";
                }
            }

            await _fileDocumentRepository.CreateAsync(file);
        }

        public async Task<EditFileDocumentResponseModel> EditFileDetailAsync(Guid id, EditFileDocumentDto dto)
        {
            var file = await _fileDocumentRepository.GetByIdAsync(id);
            if (file == null || file.IsRemoved)
                throw new ErrorException(StatusCodeEnum.A02);

            if (string.IsNullOrWhiteSpace(dto.FileName))
                throw new ErrorException(StatusCodeEnum.A04);

            if (dto.FileName.Length > 255)
                throw new ErrorException(StatusCodeEnum.A05);


            if (dto.Tags == null || !dto.Tags.Any())
                throw new ErrorException(StatusCodeEnum.A06);

            if (string.IsNullOrWhiteSpace(dto.Category.ToString()))
                throw new ErrorException(StatusCodeEnum.A07);

            // check if file with same name already exists
            var nameExisted = await _fileDocumentRepository.ExistsAsync(x =>
                x.Id != id && !x.IsDeleted && x.FileName.ToLower() == dto.FileName.ToLower());

            if (nameExisted)
                throw new ErrorException(StatusCodeEnum.A08);

            file.FileName = dto.FileName;
            file.Category = dto.Category;
            file.Description = dto.Description;
            file.Tags = dto.Tags ?? new List<string>();
            file.IsActive = dto.IsActive;
            file.IsLectureVideo = dto.IsLectureVideo;
            file.IsTextbook = dto.IsTextbook;
            file.OrgId = dto.OrgId;
            file.UpdatedAt = DateTime.UtcNow;
            file.UpdatedBy = _userContext.UserId;

            // upload a new file
            if (dto.File != null && dto.File.Length > 0)
            {
                file.FileType = dto.File.ContentType;
                file.Size = dto.File.Length;

                var checksum = await FileHelper.ComputeChecksumAsync(dto.File.OpenReadStream());
                var exists = await _fileDocumentRepository.ExistsAsync(f => f.Checksum == checksum && f.Id != file.Id && !f.IsRemoved);
                if (exists)
                    throw new ErrorException(StatusCodeEnum.A03);

                // Xóa file cũ trên MinIO
                await _minioService.RemoveFileAsync(file.Url.TrimStart('/'));

                //xóa thumbnail và hls cũ nếu là video
                if (file.IsLectureVideo)
                {
                    if (!string.IsNullOrEmpty(file.ThumbnailUrl))
                    {
                        await _minioService.RemoveFileAsync(file.ThumbnailUrl.TrimStart('/'));
                    }
                    if (!string.IsNullOrEmpty(file.HlsUrl))
                    {
                        // Xóa master playlist và các segment
                        var hlsBasePath = file.HlsUrl.Replace("master.m3u8", "").TrimEnd('/');
                        await _minioService.RemoveFileAsync(hlsBasePath + "master.m3u8");
                        for (int i = 0; i <= 5; i++)
                        {
                            var segmentPrefix = $"{hlsBasePath}v{i}/segment";
                            await _minioService.RemoveFileAsync($"{segmentPrefix}0.ts"); // Xóa ít nhất 1 segment để test
                        }
                    }
                }

                // Tải file mới lên MinIO
                var tempFilePath = Path.GetTempFileName();
                await using (var stream = new FileStream(tempFilePath, FileMode.Create))
                {
                    await dto.File.CopyToAsync(stream);
                }
                await _minioService.UploadFileAsync(file.Url.TrimStart('/'), tempFilePath, dto.File.ContentType);

                if (dto.IsLectureVideo)
                {
                    try
                    {
                        var result = await _ffmpegService.ProcessVideoAsync(dto.File);
                        file.ThumbnailUrl = result.ThumbnailUrl;
                        file.TranscodingStatus = result.Status;
                        file.Duration = result.Duration;
                        file.ViewCount ??= 0;
                        file.Rating ??= 0;
                        file.HlsUrl = result.HlsUrl; // Lưu đường dẫn HLS nếu có
                    }
                    catch
                    {
                        file.TranscodingStatus = "failed";
                    }
                }
                else
                {
                    // Nếu không phải video, xóa thumbnail và HLS cũ nếu tồn tại
                    if (!string.IsNullOrEmpty(file.ThumbnailUrl))
                        await _minioService.RemoveFileAsync(file.ThumbnailUrl.TrimStart('/'));
                    if (!string.IsNullOrEmpty(file.HlsUrl))
                        await _minioService.RemoveFileAsync(file.HlsUrl.TrimStart('/'));
                    file.ThumbnailUrl = null;
                    file.HlsUrl = null;
                    file.Duration = null;
                    file.TranscodingStatus = null;
                }

                // Xóa tệp tạm
                if (File.Exists(tempFilePath))
                {
                    try
                    {
                        File.Delete(tempFilePath);
                        Console.WriteLine($"Deleted temp file: {tempFilePath}");
                    }
                    catch (IOException ex)
                    {
                        Console.WriteLine($"Failed to delete file {tempFilePath}: {ex.Message}");
                        throw;
                    }
                }
            }

            await _fileDocumentRepository.UpdateAsync(file);

            var presignedUrl = await _minioService.GetFileAsync(file.Url.TrimStart('/'));

            return new EditFileDocumentResponseModel
            {
                Id = file.Id,
                FileName = file.FileName,
                Description = file.Description,
                Category = file.Category,
                Tags = file.Tags,
                IsActive = file.IsActive,
                IsLectureVideo = file.IsLectureVideo,
                IsTextbook = file.IsTextbook,
                OrgId = file.OrgId,
                FileType = file.FileType,
                Url = presignedUrl,
                Size = file.Size,
                Checksum = file.Checksum,
                ThumbnailUrl = file.ThumbnailUrl,
                TranscodingStatus = file.TranscodingStatus,
                AssociatedResourceIds = file.AssociatedResourceIds,
                ViewCount = file.ViewCount,
                Rating = file.Rating,
                Duration = file.Duration,
                HlsUrl = file.HlsUrl
            };
        }

        public async Task DeleteAsync(Guid id)
        {
            var file = await _fileDocumentRepository.GetByIdAsync(id);
            if (file == null || file.IsRemoved)
                throw new ErrorException(StatusCodeEnum.A02);

            // Xóa file trên MinIO
            await _minioService.RemoveFileAsync(file.Url.TrimStart('/'));

            // Xóa thumbnail và HLS nếu là video
            if (file.IsLectureVideo)
            {
                if (!string.IsNullOrEmpty(file.ThumbnailUrl))
                    await _minioService.RemoveFileAsync(file.ThumbnailUrl.TrimStart('/'));
                if (!string.IsNullOrEmpty(file.HlsUrl))
                {
                    var hlsBasePath = file.HlsUrl.Replace("master.m3u8", "").TrimEnd('/');
                    await _minioService.RemoveFileAsync(hlsBasePath + "master.m3u8");
                    for (int i = 0; i <= 5; i++)
                    {
                        var segmentPrefix = $"{hlsBasePath}v{i}/segment";
                        await _minioService.RemoveFileAsync($"{segmentPrefix}0.ts"); // Xóa ít nhất 1 segment
                    }
                }
            }

            //soft delete
            file.IsRemoved = true;
            file.DeletedAt = DateTime.UtcNow;
            file.DeletedBy = _userContext.UserId;
            await _fileDocumentRepository.UpdateAsync(file);
        }
    }
}
