using FileService.Core.ApiModels;
using FileService.Core.Enums;
using FileService.Core.Exceptions;
using FileService.Core.Helpers;
using FileService.DataAccess.Interfaces;
using FileService.DataAccess.Models;
using FileService.Service.ApiModels.FileDocumentModels;
using FileService.Service.ApiModels.TagModels;
using FileService.Service.ApiModels.UserModels;
using FileService.Service.Dtos.FileDocumentDtos;
using FileService.Service.Interfaces;
using Microsoft.AspNetCore.Mvc.RazorPages;
using MongoDB.Driver;
using System.Diagnostics;
using System.Drawing;
using System.Net.Http;
using System.Text.Json;

namespace FileService.Service.Implementation
{
    public class FileDocumentService : BaseService, IFileDocumentService
    {
        private readonly IRepository<FileDocument> _fileDocumentRepository;
        private readonly UserContext _userContext;
        private readonly IFfmpegService _ffmpegService;
        private readonly IMinioService _minioService;
        private readonly ITagService _tagService;
        private readonly IRepository<Tags> _tagRepository;
        private readonly IIdentityServiceClient _identityServiceClient;

        public FileDocumentService(
            AppSettings appSettings,
            UserContext userContext,
            IFfmpegService ffmpegService,
            IMinioService minioService,
            ITagService tagService,
            IRepository<FileDocument> fileDocumentRepository,
            IRepository<Tags> tagRepository,
            IIdentityServiceClient identityServiceClient
            )
             : base(appSettings, userContext)
        {
            _fileDocumentRepository = fileDocumentRepository;
            _userContext = userContext;
            _ffmpegService = ffmpegService;
            _minioService = minioService;
            _tagService = tagService;
            _tagRepository = tagRepository;
            _identityServiceClient = identityServiceClient;
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

            var items = await _fileDocumentRepository.FilterAsync(x => !x.IsDeleted && x.IsActive && !x.IsRemoved);

            items = items.OrderByDescending(x => x.CreatedAt).ToList();

            // Lấy tất cả TagIds từ các file
            var allTagIds = items.SelectMany(f => f.TagIds).Distinct().ToList();
            var tags = allTagIds.Any()
                ? await _tagRepository.FilterAsync(Builders<Tags>.Filter.In(t => t.Id, allTagIds))
                : new List<Tags>();

            var tagDict = tags.ToDictionary(t => t.Id, t => new TagModel { Id = t.Id, Name = t.Label });

            //call API Profile Service
            var users = await _identityServiceClient.GetAllUserProfilesAsync();
            var userDict = users.ToDictionary(u => Guid.Parse(u.UserId), u => u);


            //build result
            var resultTasks = items.Select(async file =>
            {
                var publicUrl = await _minioService.GetPublicFileUrlAsync(file.Url.TrimStart('/'));

                return new FileDocumentModel
                {
                    Id = file.Id,
                    FileName = file.FileName,
                    FileType = file.FileType,
                    Url = publicUrl,
                    Size = file.Size,
                    Checksum = file.Checksum,
                    Category = file.Category,
                    IsActive = file.IsActive,
                    Tags = file.TagIds.Select(id => tagDict.ContainsKey(id) ? tagDict[id] : new TagModel { Id = id, Name = "[Unknown]" }).ToList(),
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
                    AssociatedResourceIds = file.AssociatedResourceIds,
                    CreatedAt = file.CreatedAt,
                    CreatedBy = file.CreatedBy,
                    OrganizationId = _userContext.OrganizationId,

                    //map CreatedBy -> User
                    UserProfile = userDict.GetValueOrDefault(file.CreatedBy, new UserProfileResponse())
                };
            });

            return await Task.WhenAll(resultTasks);

        }

        public async Task<List<FileDocumentModel>> GetVideosAsync()
        {
            var videos = await _fileDocumentRepository.FilterAsync(f => f.Category == FileCategory.Video && !f.IsDeleted && f.IsLectureVideo == true);
            if (videos == null || !videos.Any())
            {
                throw new ErrorException(StatusCodeEnum.D01);
            }

            videos = videos.OrderByDescending(x => x.CreatedAt).ToList();

            //call API Profile Service
            var users = await _identityServiceClient.GetAllUserProfilesAsync();
            //var userDict = users.ToDictionary(u => Guid.Parse(u.UserId), u => u);
            var userDict = users
                .Where(u => Guid.TryParse(u.UserId, out _))
                .ToDictionary(u => Guid.Parse(u.UserId), u => u);

            //build result
            var resultTasks = videos.Select(async videosItem =>
            {
                var publicUrl = await _minioService.GetPublicFileUrlAsync(videosItem.Url.TrimStart('/'));
                return new FileDocumentModel
                {
                    Id = videosItem.Id,
                    FileName = videosItem.FileName,
                    FileType = videosItem.FileType,
                    Url = publicUrl,
                    Size = videosItem.Size,
                    Checksum = videosItem.Checksum,
                    Category = videosItem.Category,
                    IsActive = videosItem.IsActive,
                    ViewCount = videosItem.ViewCount,
                    Rating = videosItem.Rating,
                    Description = videosItem.Description,
                    OrgId = videosItem.OrgId,
                    ThumbnailUrl = videosItem.ThumbnailUrl,
                    HlsUrl = videosItem.HlsUrl,
                    Duration = videosItem.Duration,
                    IsLectureVideo = videosItem.IsLectureVideo,
                    IsTextbook = videosItem.IsTextbook,
                    TranscodingStatus = videosItem.TranscodingStatus,
                    AssociatedResourceIds = videosItem.AssociatedResourceIds,
                    CreatedAt = videosItem.CreatedAt,
                    CreatedBy = videosItem.CreatedBy,
                    OrganizationId = _userContext.OrganizationId,

                    //map CreatedBy -> User
                    UserProfile = userDict.GetValueOrDefault(videosItem.CreatedBy, new UserProfileResponse())
                };
            });
            return (await Task.WhenAll(resultTasks)).ToList();
        }

        public async Task<List<FileDocumentModel>> GetRegularFilesAsync()
        {
            var files = await _fileDocumentRepository.FilterAsync(f => f.Category == FileCategory.RegularFile && !f.IsDeleted);
            if (files == null || !files.Any())
            {
                throw new ErrorException(StatusCodeEnum.D01);
            }

            files = files.OrderByDescending(x => x.CreatedAt).ToList();

            //call API Profile Service
            var users = await _identityServiceClient.GetAllUserProfilesAsync();
            var userDict = users.ToDictionary(u => Guid.Parse(u.UserId), u => u);

            //build result
            var resultTasks = files.Select(async filesItem =>
            {
                var publicUrl = await _minioService.GetPublicFileUrlAsync(filesItem.Url.TrimStart('/'));
                return new FileDocumentModel
                {
                    Id = filesItem.Id,
                    FileName = filesItem.FileName,
                    FileType = filesItem.FileType,
                    Url = publicUrl,
                    Size = filesItem.Size,
                    Checksum = filesItem.Checksum,
                    Category = filesItem.Category,
                    IsActive = filesItem.IsActive,
                    ViewCount = filesItem.ViewCount,
                    Rating = filesItem.Rating,
                    Description = filesItem.Description,
                    OrgId = filesItem.OrgId,
                    ThumbnailUrl = filesItem.ThumbnailUrl,
                    HlsUrl = filesItem.HlsUrl,
                    Duration = filesItem.Duration,
                    IsLectureVideo = filesItem.IsLectureVideo,
                    IsTextbook = filesItem.IsTextbook,
                    TranscodingStatus = filesItem.TranscodingStatus,
                    AssociatedResourceIds = filesItem.AssociatedResourceIds,
                    CreatedAt = filesItem.CreatedAt,
                    CreatedBy = filesItem.CreatedBy,
                    OrganizationId = _userContext.OrganizationId,

                    //map CreatedBy -> User
                    UserProfile = userDict.GetValueOrDefault(filesItem.CreatedBy, new UserProfileResponse())

                };
            });
            return (await Task.WhenAll(resultTasks)).ToList();
        }
        public async Task<FileDocumentModel> GetFileDetailById(Guid fileId)
        {
            var file = await _fileDocumentRepository.GetByIdAsync(fileId);
            if (file == null || file.IsRemoved)
                throw new ErrorException(StatusCodeEnum.A02);

            var tags = file.TagIds.Any()
                ? await _tagRepository.FilterAsync(Builders<Tags>.Filter.In(t => t.Id, file.TagIds))
                : new List<Tags>();

            var tagDict = tags.ToDictionary(t => t.Id, t => new TagModel { Id = t.Id, Name = t.Label });

            //lấy thông tin người tạo
            var user = await _identityServiceClient.GetUserProfileAsync(file.CreatedBy.ToString());

            // Tạo publicUrl URL từ MinIO
            var publicUrl = await _minioService.GetPublicFileUrlAsync(file.Url.TrimStart('/')); // Loại bỏ '/' đầu nếu có

            var result = new FileDocumentModel
            {
                Id = file.Id,
                FileName = file.FileName,
                FileType = file.FileType,
                Url = publicUrl,
                Size = file.Size,
                Checksum = file.Checksum,
                Category = file.Category,
                IsActive = file.IsActive,
                Tags = file.TagIds.Select(id => tagDict.ContainsKey(id) ? tagDict[id] : new TagModel { Id = id, Name = "[Unknown]" }).ToList(),
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
                HlsUrl = file.HlsUrl,
                CreatedAt = file.CreatedAt,
                CreatedBy = file.CreatedBy,
                OrganizationId = _userContext.OrganizationId,
                UserProfile = user ?? new UserProfileResponse()
            };

            return result;
        }

        public async Task<FileUploadResult> AddFileAsync(AddFileDocumentDto dto)
        {
            if (dto.File.FileName.Length > 255)
                throw new ErrorException(StatusCodeEnum.A05);

            if (string.IsNullOrWhiteSpace(dto.Category.ToString()))
                throw new ErrorException(StatusCodeEnum.A07);

            var fileId = Guid.NewGuid();

            var ext = Path.GetExtension(dto.File.FileName);
            var fileName = $"files/{fileId}{ext}";

            using (var stream = dto.File.OpenReadStream())
            {
                if (stream.Length == 0)
                    throw new ErrorException(StatusCodeEnum.A01, "Source file is empty");

                await _minioService.UploadStreamAsync(fileName, stream, dto.File.ContentType);
            }

            var tagIds = await _tagService.GetOrCreateTagIdsAsync(dto.Tags ?? Enumerable.Empty<string>());

            var file = new FileDocument
            {
                Id = fileId,
                FileName = dto.File.FileName,
                FileType = dto.File.ContentType,
                Size = dto.File.Length,
                Url = fileName,
                Description = dto.Description,
                Category = dto.Category,
                TagIds = tagIds,
                IsActive = true,
                CreatedAt = DateTime.UtcNow,
                CreatedBy = _userContext.UserId,
                OrgId = dto?.OrgId.HasValue == true && dto.OrgId != Guid.Empty ? dto.OrgId : null,
                IsLectureVideo = dto.IsLectureVideo,
                IsTextbook = dto.IsTextbook,
            };

            // Tính toán checksum của file
            await using (var checksumStream = dto.File.OpenReadStream())
            {
                file.Checksum = await FileHelper.ComputeChecksumAsync(checksumStream);
            }

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

            var publicUrl = await _minioService.GetPublicFileUrlAsync(file.Url);

            return new FileUploadResult
            {
                FileId = file.Id,
                Url = publicUrl,
            };
        }

        public async Task<EditFileDocumentResponseModel> EditFileDetailAsync(Guid id, EditFileDocumentDto dto)
        {
            var file = await _fileDocumentRepository.GetByIdAsync(id);
            if (file == null || file.IsRemoved)
                throw new ErrorException(StatusCodeEnum.A02);

            if (dto.FileName.Length > 255)
                throw new ErrorException(StatusCodeEnum.A05);

            if (string.IsNullOrWhiteSpace(dto.Category.ToString()))
                throw new ErrorException(StatusCodeEnum.A07);

            file.FileName = dto.FileName;
            file.Category = dto.Category;
            file.Description = dto.Description;
            file.TagIds = await _tagService.GetOrCreateTagIdsAsync(dto.Tags ?? Enumerable.Empty<string>());
            file.IsActive = dto.IsActive;
            file.IsLectureVideo = dto.IsLectureVideo;
            file.IsTextbook = dto.IsTextbook;
            file.OrgId = string.IsNullOrWhiteSpace(dto.OrgId?.ToString()) ? null : dto.OrgId;
            file.UpdatedAt = DateTime.UtcNow;
            file.UpdatedBy = _userContext.UserId;

            // upload a new file
            if (dto.File != null && dto.File.Length > 0)
            {
                file.FileType = dto.File.ContentType;
                file.Size = dto.File.Length;

                var checksum = await FileHelper.ComputeChecksumAsync(dto.File.OpenReadStream());

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
                    try
                    {
                        await dto.File.CopyToAsync(stream);
                    }
                    catch (Exception ex)
                    {
                        throw new Exception($"Failed to copy file to temp location {tempFilePath}: {ex.Message}", ex);
                    }
                }

                using (var newStream = dto.File.OpenReadStream())
                {
                    await _minioService.UploadStreamAsync(file.Url.TrimStart('/'), newStream, dto.File.ContentType);
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

            var publicUrl = await _minioService.GetPublicFileUrlAsync(file.Url.TrimStart('/'));

            var tags = file.TagIds.Any()
                ? await _tagRepository.FilterAsync(Builders<Tags>.Filter.In(t => t.Id, file.TagIds))
                : new List<Tags>();

            var tagDict = tags.ToDictionary(t => t.Id, t => new TagModel { Id = t.Id, Name = t.Label });


            return new EditFileDocumentResponseModel
            {
                Id = file.Id,
                FileName = file.FileName,
                Description = file.Description,
                Category = file.Category,
                Tags = file.TagIds.Select(id => tagDict.ContainsKey(id) ? tagDict[id] : new TagModel { Id = id, Name = "[Unknown]" }).ToList(),
                IsActive = file.IsActive,
                IsLectureVideo = file.IsLectureVideo,
                IsTextbook = file.IsTextbook,
                OrgId = file.OrgId,
                FileType = file.FileType,
                Url = publicUrl,
                Size = file.Size,
                Checksum = file.Checksum,
                ThumbnailUrl = file.ThumbnailUrl,
                TranscodingStatus = file.TranscodingStatus,
                AssociatedResourceIds = file.AssociatedResourceIds,
                ViewCount = file.ViewCount,
                Rating = file.Rating,
                Duration = file.Duration,
                HlsUrl = file.HlsUrl,
                UpdatedAt = file.UpdatedAt,
                UpdatedBy = file.UpdatedBy,
                Roles = _userContext.Roles,
                OrganizationRole = _userContext.OrganizationRole,
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
            file.IsActive = false;
            file.IsDeleted = true;
            file.IsRemoved = true;
            file.DeletedAt = DateTime.UtcNow;
            file.DeletedBy = _userContext.UserId;
            await _fileDocumentRepository.UpdateAsync(file);
        }
    }
}
