using FileService.Core.ApiModels;
using FileService.Service.ApiModels.FileDocumentModels;
using FileService.Service.Dtos.FileDocumentDtos;
using FileService.Service.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace FileService.Api.Controllers
{
    [Authorize]
    [AllowAnonymous]
    [Authorize(Policy = "AdminOnly")]
    // [Authorize(Policy = "Permission")]
    // [Authorize(Roles = "ADMIN")]
    [Route("file/api/[controller]")]
    [ApiController]
    public class FileDocumentController : BaseApiController
    {
        public IFileDocumentService _fileDocumentService { get; set; }
        public IFfmpegService _ffmpegService { get; set; }
        public FileDocumentController(IServiceProvider serviceProvider, IFileDocumentService fileDocumentService, IFfmpegService ffmpegService  ) : base(serviceProvider)
        {
            _fileDocumentService = fileDocumentService;
            _ffmpegService = ffmpegService;
        }

        [HttpPost("public")]
        public async Task<IActionResult> GetViewModelsAsync([FromBody] FileDocumentDto pagingDto)
        {
            var result = await _fileDocumentService.GetViewModelsAsync(pagingDto);
            var listResult = new PaginatedList<FileDocumentModel>(result.ToList(), result.Count(), pagingDto.PageIndex, pagingDto.PageSize);
            return Success(listResult);
        }

        [HttpGet("videos")]
        public async Task<IActionResult> GetVideos()
        {
            var files = await _fileDocumentService.GetVideosAsync();
            return Success(files);
        }

        [HttpGet("regular-files")]
        public async Task<IActionResult> GetRegularFiles()
        {
            var files = await _fileDocumentService.GetRegularFilesAsync();
            return Success(files);
        }

        [HttpGet("file/{fileId}")]
        public async Task<IActionResult> GetFileById(Guid fileId)
        {
            var result = await _fileDocumentService.GetFileDetailById(fileId);
            return Success(result);
        }

        [HttpGet("video/{videoId}")]
        public async Task<IActionResult> GetVideoById(Guid videoId)
        {
            var result = await _fileDocumentService.GetFileDetailById(videoId);
            return Success(result);
        }

        [HttpPost("add")]
        public async Task<IActionResult> AddFile([FromForm] AddFileDocumentDto dto)
        {
            var result = await _fileDocumentService.AddFileAsync(dto);
            return Success(result);
        }

        [HttpPut("edit/{id}")]
        public async Task<IActionResult> EditDetailFile(Guid id, [FromForm] EditFileDocumentDto dto)
        {
            var result = await _fileDocumentService.EditFileDetailAsync(id, dto);
            return Success(result);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteFile(Guid id)
        {
            await _fileDocumentService.DeleteAsync(id);
            return Success("Xóa thành công!");
        }
    }
}
