using FileService.DataAccess.Models;
using FileService.Service.ApiModels.TagModels;
using FileService.Service.Dtos.TagDtos;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileService.Service.Interfaces
{
    public interface ITagService
    {
        Task<IEnumerable<TagModel>> GetViewModelsAsync(TagDto tagDto);
        Task<TagModel> GetTagDetailById(Guid id);
        Task AddTagAsync(string label);
        Task<List<Guid>> GetOrCreateTagIdsAsync(IEnumerable<string> labels);
        Task<EditTagResponseModel> UpdateTagAsync(Guid tagId, string newLabel);
    }
}
