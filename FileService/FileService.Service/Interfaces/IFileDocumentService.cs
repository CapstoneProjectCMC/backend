using FileService.Core.ApiModels;
using FileService.DataAccess.Models;
using FileService.Service.ApiModels.FileDocumentModels;
using FileService.Service.Dtos.FileDocumentDtos;

namespace FileService.Service.Interfaces
{
    public interface IFileDocumentService
    {
        Task<IEnumerable<FileDocumentModel>> GetViewModelsAsync(FileDocumentDto fileDocumentDto);
        Task AddFileAsync(AddFileDocumentDto addFileDto);
        Task<FileDocumentModel> GetFileDetailById(Guid id);
        Task<EditFileDocumentResponseModel> EditFileDetailAsync(Guid id, EditFileDocumentDto editFileDto);
        Task DeleteAsync(Guid id); 
    }
}
