using FileService.Core.ApiModels;
using FileService.Service.ApiModels.FileDocumentModels;
using FileService.Service.Interfaces;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Options;
using Xabe.FFmpeg;

namespace FileService.Service.Implementation
{
    public class FfmpegService : IFfmpegService
    {
        private readonly string _outputFolder;
        public FfmpegService(IOptions<FfmpegSettings> settings)
        {
            var fullPath = Path.Combine(AppContext.BaseDirectory, settings.Value.ExecutablePath);
            FFmpeg.SetExecutablesPath(fullPath);

            _outputFolder = Path.Combine("wwwroot", "thumbnails");
            Directory.CreateDirectory(_outputFolder);
        }
        public async Task<VideoProcessResultModel> ProcessVideoAsync(IFormFile videoFile)
        {   
            if (videoFile == null || videoFile.Length == 0)
                throw new ArgumentException("Invalid video file.");

            var fileName = Guid.NewGuid() + Path.GetExtension(videoFile.FileName);
            var tempInputPath = Path.Combine(Path.GetTempPath(), fileName);
            var outputImagePath = Path.Combine(_outputFolder, $"{Path.GetFileNameWithoutExtension(fileName)}.jpg");

            await using (var stream = new FileStream(tempInputPath, FileMode.Create))
            {
                await videoFile.CopyToAsync(stream);
            }

            try
            {
                var mediaInfo = await FFmpeg.GetMediaInfo(tempInputPath);

                if (!mediaInfo.VideoStreams.Any())
                    throw new Exception("No video stream found.");

                var videoStream = mediaInfo.VideoStreams.First();

                var conversion = await FFmpeg.Conversions.FromSnippet.Snapshot(tempInputPath, outputImagePath, TimeSpan.FromSeconds(1));
                await conversion.Start();

                return new VideoProcessResultModel
                {
                    ThumbnailUrl = $"/thumbnails/{Path.GetFileName(outputImagePath)}",
                    Duration = videoStream.Duration,
                    Status = "success"
                };
            }
            catch (Exception ex)
            {
                Console.WriteLine("FFmpeg processing failed: " + ex.Message);
                Console.WriteLine("StackTrace: " + ex.StackTrace);

                return new VideoProcessResultModel
                {
                    ThumbnailUrl = null,
                    Duration = null,
                    Status = "failed"
                };
            }
            finally
            {
                if (File.Exists(tempInputPath))
                    File.Delete(tempInputPath);
            }
        }
    }

}
