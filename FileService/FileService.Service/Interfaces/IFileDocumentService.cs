using FileService.Core.ApiModels;
using FileService.Core.Enums;
using FileService.DataAccess.Models;
using FileService.Service.ApiModels.FileDocumentModels;
using FileService.Service.Dtos.FileDocumentDtos;

namespace FileService.Service.Interfaces
{
    public interface IFileDocumentService
    {
        Task<IEnumerable<FileDocumentModel>> GetViewModelsAsync(FileDocumentDto fileDocumentDto);
        Task<FileUploadResult> AddFileAsync(AddFileDocumentDto addFileDto);
        Task<FileDocumentModel> GetFileDetailById(Guid fileId);
        Task<FileDocumentModel> GetVideoDetailById(Guid videoId);
        Task<EditFileDocumentResponseModel> EditFileDetailAsync(Guid id, EditFileDocumentDto editFileDto);
        Task DeleteAsync(Guid id);
        Task<List<FileDocumentModel>> GetVideosAsync();
        Task<List<FileDocumentModel>> GetRegularFilesAsync();

    }
}
