using FileService.Core.ApiModels;
using FileService.Core.Enums;
using FileService.Core.Exceptions;
using FileService.Core.Helpers;
using FileService.DataAccess.Implementation;
using FileService.DataAccess.Interfaces;
using FileService.DataAccess.Models;
using FileService.Service.ApiModels.FileDocumentModels;
using FileService.Service.Dtos.FileDocumentDtos;
using FileService.Service.Interfaces;

namespace FileService.Service.Implementation
{
    public class FileDocumentService : BaseService, IFileDocumentService
    {
        private readonly IRepository<FileDocument> _fileDocumentRepository;
        private readonly UserContext _userContext;
        private readonly IFfmpegService _ffmpegService;

        public FileDocumentService(
            AppSettings appSettings,
            UserContext userContext,
            IFfmpegService ffmpegService,
            IRepository<FileDocument> fileDocumentRepository)
             : base(appSettings, userContext)
        {
            _fileDocumentRepository = fileDocumentRepository;
            _userContext = userContext;
            _ffmpegService = ffmpegService;
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

            return new FileDocumentModel
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
                IsLectureVideo = file.IsLectureVideo,
                IsTextbook = file.IsTextbook,
                TranscodingStatus = file.TranscodingStatus,
                AssociatedResourceIds = file.AssociatedResourceIds
            };
        }

        public async Task AddFileAsync(AddFileDocumentDto dto)
        {
            if (dto.File == null || dto.File.Length == 0)
                throw new ErrorException(StatusCodeEnum.A01);

            var fileId = Guid.NewGuid();

            var ext = Path.GetExtension(dto.File.FileName);
            var fileName = fileId + ext;

            //save file to BE folder 
            var staticPath = Path.Combine("wwwroot", "static");
            if (!Directory.Exists(staticPath))
            {
                Directory.CreateDirectory(staticPath);
            }

            var filePath = Path.Combine(staticPath, fileName);
            await using (var stream = new FileStream(filePath, FileMode.Create))
            {
                await dto.File.CopyToAsync(stream);
            }

            var file = new FileDocument
            {
                Id = fileId,
                FileName = dto.File.FileName,
                FileType = dto.File.ContentType,
                Size = dto.File.Length,
                Url = $"/static/{fileName}",
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

            file.Checksum = await FileHelper.ComputeChecksumAsync(dto.File.OpenReadStream());

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
                file.Checksum = await FileHelper.ComputeChecksumAsync(dto.File.OpenReadStream());

                // save to override file in the static folder
                var savePath = Path.Combine("wwwroot", "static", file.Id.ToString());
                using (var stream = new FileStream(savePath, FileMode.Create))
                {
                    await dto.File.CopyToAsync(stream);
                }

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
                    }
                    catch
                    {
                        file.TranscodingStatus = "failed";
                    }
                }
            }

            await _fileDocumentRepository.UpdateAsync(file);

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
                Url = file.Url,
                Size = file.Size,
                Checksum = file.Checksum,
                ThumbnailUrl = file.ThumbnailUrl,
                TranscodingStatus = file.TranscodingStatus,
                AssociatedResourceIds = file.AssociatedResourceIds,
                ViewCount = file.ViewCount,
                Rating = file.Rating
            };
        }

        public async Task DeleteAsync(Guid id)
        {
            var file = await _fileDocumentRepository.GetByIdAsync(id);
            if (file == null)
                throw new ErrorException(StatusCodeEnum.A02);

            file.IsRemoved = true;
            file.DeletedAt = DateTime.UtcNow;
            file.DeletedBy = _userContext.UserId;
            await _fileDocumentRepository.DeleteAsync(id);
        }
    }
}
