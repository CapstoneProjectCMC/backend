using FileService.Core.ApiModels;
using FileService.Core.Enums;
using FileService.Core.Exceptions;
using FileService.DataAccess.Interfaces;
using FileService.DataAccess.Models;
using FileService.Service.ApiModels.TagModels;
using FileService.Service.Dtos.TagDtos;
using FileService.Service.Interfaces;

namespace FileService.Service.Implementation
{
    public class TagService : BaseService, ITagService
    {
        private readonly IRepository<Tags> _tagRepository;
        private readonly UserContext _userContext;
        private readonly IFfmpegService _ffmpegService;
        private readonly IMinioService _minioService;


        public TagService(
            AppSettings appSettings,
            UserContext userContext,
            IFfmpegService ffmpegService,
            IMinioService minioService,
            IRepository<Tags> tagRepository)
             : base(appSettings, userContext)
        {
            _tagRepository = tagRepository;
            _userContext = userContext;
            _ffmpegService = ffmpegService;
            _minioService = minioService;
        }

        private void ValidatePagingModel(TagDto tagDto)
        {
            if (tagDto.PageIndex < 1)
                throw new ErrorException(Core.Enums.StatusCodeEnum.PageIndexInvalid);
            if (tagDto.PageSize < 1)
                throw new ErrorException(Core.Enums.StatusCodeEnum.PageSizeInvalid);
        }

        public async Task<IEnumerable<TagModel>> GetViewModelsAsync(TagDto tagDto)
        {
            // Validate PageIndex and PageSize
            ValidatePagingModel(tagDto);

            var items = await _tagRepository.FilterAsync(x => !x.IsDeleted);

            return items.Select(tag => new TagModel
            {
                Id = tag.Id,
                Name = tag.Label
            });
        }

        public async Task<TagModel> GetTagDetailById(Guid id)
        {
            var tag = await _tagRepository.GetByIdAsync(id);
            if (tag == null || tag.IsDeleted)
                throw new ErrorException(StatusCodeEnum.A02);

            return new TagModel
            {
                Id = tag.Id,
                Name = tag.Label
            };
        }

        public async Task AddTagAsync(string label)
        {
            var existingTag = await _tagRepository.ExistsAsync(t => t.Label == label && !t.IsDeleted);
            if (existingTag)
                throw new ErrorException(StatusCodeEnum.A08);

            var tag = new Tags { Label = label };
            await _tagRepository.CreateAsync(tag);
        }

        public async Task<List<Guid>> GetOrCreateTagIdsAsync(IEnumerable<string> labels)
        {
            if (labels == null || !labels.Any())
                return new List<Guid>();

            var tagIds = new List<Guid>();
            var existingTags = await _tagRepository.FilterAsync(t => labels.Contains(t.Label) && !t.IsDeleted);
            var existingLabels = existingTags.Select(t => t.Label).ToList();
            var newLabels = labels.Except(existingLabels).ToList();

            foreach (var label in newLabels)
            {
                if (string.IsNullOrWhiteSpace(label))
                    throw new ErrorException(StatusCodeEnum.A04);

                var existingTag = await _tagRepository.ExistsAsync(t => t.Label.ToLower() == label.ToLower() && !t.IsDeleted);
                if (existingTag)
                    throw new ErrorException(StatusCodeEnum.A08);

                var tag = new Tags
                {
                    Id = Guid.NewGuid(),
                    Label = label,
                    CreatedAt = DateTime.UtcNow,
                };
                await _tagRepository.CreateAsync(tag);
                tagIds.Add(tag.Id);
            }

            tagIds.AddRange(existingTags.Select(t => t.Id));
            return tagIds;
        }

        public async Task<EditTagResponseModel> UpdateTagAsync(Guid tagId, string newLabel)
        {
            var tag = await _tagRepository.GetByIdAsync(tagId);

            if (tag == null || tag.IsDeleted)
                throw new ErrorException(StatusCodeEnum.A02);

            var nameExisted = await _tagRepository.ExistsAsync(t => t.Label == newLabel);

            if (nameExisted)
                throw new ErrorException(StatusCodeEnum.A08);
            
            tag.Label = newLabel;
            await _tagRepository.UpdateAsync(tag);

            return new EditTagResponseModel
            {
                Id = tag.Id,
                Lable = tag.Label
            };
        }
    }
}
