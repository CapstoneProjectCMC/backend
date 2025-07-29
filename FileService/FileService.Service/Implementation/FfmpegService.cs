using FileService.Core.ApiModels;
using FileService.Service.ApiModels.FileDocumentModels;
using FileService.Service.Interfaces;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Options;
using System.Diagnostics;

namespace FileService.Service.Implementation
{
    public class FfmpegService : IFfmpegService
    {
        private readonly IMinioService _minioService;
        private readonly string _tempFolder;

        public FfmpegService(IOptions<FfmpegSettings> settings, IMinioService minioService)
        {
            _minioService = minioService;
            _tempFolder = Path.GetTempPath(); // Sử dụng thư mục tạm để xử lý
            Directory.CreateDirectory(_tempFolder);
        }

        private TimeSpan? GetVideoDuration(string videoPath)
        {
            var startInfo = new ProcessStartInfo
            {
                FileName = "ffprobe",
                Arguments = $"-v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 \"{videoPath}\"",
                RedirectStandardOutput = true,
                RedirectStandardError = true,
                UseShellExecute = false,
                CreateNoWindow = true
            };

            using var process = Process.Start(startInfo);
            string output = process.StandardOutput.ReadToEnd();
            process.WaitForExit();

            if (double.TryParse(output.Trim(), out double seconds))
            {
                return TimeSpan.FromSeconds(seconds);
            }

            return null;
        }


        private async Task RunFfmpegAsync(string arguments)
        {
            var startInfo = new ProcessStartInfo
            {
                FileName = "ffmpeg", 
                Arguments = arguments,
                RedirectStandardOutput = true,
                RedirectStandardError = true,
                UseShellExecute = false,
                CreateNoWindow = true
            };

            using var process = new Process { StartInfo = startInfo };
            process.Start();

            string stderr = await process.StandardError.ReadToEndAsync();
            string stdout = await process.StandardOutput.ReadToEndAsync();

            await process.WaitForExitAsync();

            if (process.ExitCode != 0)
                throw new Exception($"FFmpeg exited with code {process.ExitCode}. Error: {stderr}");
        }


        public async Task<VideoProcessResultModel> ProcessVideoAsync(IFormFile videoFile)
        {   
            if (videoFile == null || videoFile.Length == 0)
                throw new ArgumentException("Invalid video file.");

            if (videoFile.Length > 500 * 1024 * 1024) // 500MB
                throw new ArgumentException("Video file too large.");

            var fileId = Guid.NewGuid().ToString();
            var ext = Path.GetExtension(videoFile.FileName);
            var tempInputPath = Path.Combine(Path.GetTempPath(), $"{fileId}{ext}");
            var outputFolder = Path.Combine(_tempFolder, fileId);
            var thumbnailPath = Path.Combine(_tempFolder, $"{fileId}.jpg");
            var masterPlaylistPath = Path.Combine(outputFolder, "master.m3u8");

            Directory.CreateDirectory(outputFolder);
            Directory.CreateDirectory(Path.GetDirectoryName(thumbnailPath)!);

            for (int i = 0; i <= 5; i++)
            {
                var resolutionFolder = Path.Combine(outputFolder, $"v{i}");
                Directory.CreateDirectory(resolutionFolder);
            }

            // Save the uploaded video file to a temporary location
            await using (var stream = new FileStream(tempInputPath, FileMode.Create))
            {
                await videoFile.CopyToAsync(stream);
            }

            try
            {
                // Create thumbnail
                var thumbnailArgs = $"-ss 00:00:01 -i \"{tempInputPath}\" -frames:v 1 -q:v 2 \"{thumbnailPath}\"";
                await RunFfmpegAsync(thumbnailArgs);

                //FFmpeg CLI arguments for HLS Adaptive Bitrate
                var ffmpegArgs = $"-i \"{tempInputPath}\" " +
                                "-filter_complex " +
                                "\"[0:v]split=6[v144][v240][v360][v480][v720][v1080]; " +
                                "[v144]scale=w=256:h=144:force_original_aspect_ratio=decrease:force_divisible_by=2[v144out]; " +
                                "[v240]scale=w=426:h=240:force_original_aspect_ratio=decrease:force_divisible_by=2[v240out]; " +
                                "[v360]scale=w=640:h=360:force_original_aspect_ratio=decrease:force_divisible_by=2[v360out]; " +
                                "[v480]scale=w=854:h=480:force_original_aspect_ratio=decrease:force_divisible_by=2[v480out]; " +
                                "[v720]scale=w=1280:h=720:force_original_aspect_ratio=decrease:force_divisible_by=2[v720out]; " +
                                "[v1080]scale=w=1920:h=1080:force_original_aspect_ratio=decrease:force_divisible_by=2[v1080out]\" " +

                                "-map \"[v144out]\" -map 0:a -c:v:0 libx264 -b:v:0 300k -profile:v:0 baseline -c:a:0 aac -b:a:0 96k " +
                                "-map \"[v240out]\" -map 0:a -c:v:1 libx264 -b:v:1 700k -profile:v:1 baseline -c:a:1 aac -b:a:1 96k " +
                                "-map \"[v360out]\" -map 0:a -c:v:2 libx264 -b:v:2 1000k -profile:v:2 main -c:a:2 aac -b:a:2 128k " +
                                "-map \"[v480out]\" -map 0:a -c:v:3 libx264 -b:v:3 1500k -profile:v:3 main -c:a:3 aac -b:a:3 128k " +
                                "-map \"[v720out]\" -map 0:a -c:v:4 libx264 -b:v:4 2500k -profile:v:4 main -c:a:4 aac -b:a:4 128k " +
                                "-map \"[v1080out]\" -map 0:a -c:v:5 libx264 -b:v:5 4000k -profile:v:5 high -c:a:5 aac -b:a:5 128k " +

                                "-f hls -var_stream_map " +
                                "\"v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3 v:4,a:4 v:5,a:5\" " +
                                $"-master_pl_name master.m3u8 -hls_time 6 -hls_list_size 0 " +
                                $"-hls_segment_filename \"{outputFolder}/v%v/segment%d.ts\" " +
                                $"\"{outputFolder}/v%v/playlist.m3u8\"";

                await RunFfmpegAsync(ffmpegArgs);

                // Upload thumbnail to MinIO
                var thumbnailObjectName = $"thumbnails/{fileId}.jpg";
                await _minioService.UploadFileAsync(thumbnailObjectName, thumbnailPath, "image/jpeg");

                // Upload HLS master playlist and segments to MinIO
                var hlsObjectName = $"videos/{fileId}/master.m3u8";
                await _minioService.UploadFileAsync(hlsObjectName, masterPlaylistPath, "application/x-mpegURL");

                // Upload all segment files
                for (int i = 0; i <= 5; i++)
                {
                    var segmentFolder = Path.Combine(outputFolder, $"v{i}");
                    var segmentFiles = Directory.GetFiles(segmentFolder, "segment*.ts");
                    foreach (var segmentFile in segmentFiles)
                    {
                        var segmentObjectName = $"videos/{fileId}/v{i}/" + Path.GetFileName(segmentFile);
                        await _minioService.UploadFileAsync(segmentObjectName, segmentFile, "video/MP2T");
                    }
                }

                // get duration using ffprobe
                var duration = GetVideoDuration(tempInputPath);
                // Get presigned URLs from MinIO
                var thumbnailUrl = await _minioService.GetFileAsync(thumbnailObjectName);
                var hlsUrl = await _minioService.GetFileAsync(hlsObjectName);

                return new VideoProcessResultModel
                {
                    ThumbnailUrl = thumbnailUrl,
                    Duration = duration,
                    Status = "success",
                    HlsUrl = hlsUrl
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
                    Status = "failed",
                    HlsUrl = null
                };
            }
            finally
            {
                // Xóa tất cả tệp tạm
                if (Directory.Exists(outputFolder))
                {
                    try
                    {
                        Directory.Delete(outputFolder, true);
                        Console.WriteLine($"Deleted temp folder: {outputFolder}");
                    }
                    catch (IOException ex)
                    {
                        Console.WriteLine($"Failed to delete temp folder {outputFolder}: {ex.Message}");
                    }
                }
                if (File.Exists(tempInputPath))
                {
                    try
                    {
                        File.Delete(tempInputPath);
                        Console.WriteLine($"Deleted temp file: {tempInputPath}");
                    }
                    catch (IOException ex)
                    {
                        Console.WriteLine($"Failed to delete temp file {tempInputPath}: {ex.Message}");
                    }
                }
                if (File.Exists(thumbnailPath))
                {
                    try
                    {
                        File.Delete(thumbnailPath);
                        Console.WriteLine($"Deleted temp thumbnail: {thumbnailPath}");
                    }
                    catch (IOException ex)
                    {
                        Console.WriteLine($"Failed to delete temp thumbnail {thumbnailPath}: {ex.Message}");
                    }
                }
            }
        }
    }
}
