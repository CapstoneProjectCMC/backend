using FileService.Service.ApiModels.FileDocumentModels;
using Microsoft.AspNetCore.Http;

namespace FileService.Service.Interfaces
{
    public interface IFfmpegService
    {
        Task<VideoProcessResultModel> ProcessVideoAsync(IFormFile videoFile);
    }
}
