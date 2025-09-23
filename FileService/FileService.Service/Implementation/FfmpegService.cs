using FileService.Core.ApiModels;
using FileService.Service.ApiModels.FileDocumentModels;
using FileService.Service.Interfaces;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using System.Diagnostics;
using System.Text;

namespace FileService.Service.Implementation
{
    public class FfmpegService : IFfmpegService
    {
        private readonly ILogger<FfmpegService> _logger;
        private readonly IMinioService _minioService;
        private readonly string _tempFolder;
        
        private bool HasAudioStream(string videoPath)
        {
            var psi = new ProcessStartInfo
            {
                FileName = "ffprobe",
                Arguments = $"-v error -select_streams a:0 -show_entries stream=codec_type -of csv=p=0 \"{videoPath}\"",
                RedirectStandardOutput = true,
                RedirectStandardError = true,
                UseShellExecute = false,
                CreateNoWindow = true
            };
            using var p = Process.Start(psi);
            string stdout = p.StandardOutput.ReadToEnd();
            p.WaitForExit();
            return !string.IsNullOrWhiteSpace(stdout); // có ít nhất 1 audio stream
        }
        
        
        private string BuildFfmpegArgs(string inputPath, string outputFolder, bool hasAudio)
        {
            // Scale 6 mức như cũ
            var filterComplex =
                "[0:v]split=6[v144][v240][v360][v480][v720][v1080];" +
                "[v144]scale=w=256:h=144:force_original_aspect_ratio=decrease:force_divisible_by=2[v144out];" +
                "[v240]scale=w=426:h=240:force_original_aspect_ratio=decrease:force_divisible_by=2[v240out];" +
                "[v360]scale=w=640:h=360:force_original_aspect_ratio=decrease:force_divisible_by=2[v360out];" +
                "[v480]scale=w=854:h=480:force_original_aspect_ratio=decrease:force_divisible_by=2[v480out];" +
                "[v720]scale=w=1280:h=720:force_original_aspect_ratio=decrease:force_divisible_by=2[v720out];" +
                "[v1080]scale=w=1920:h=1080:force_original_aspect_ratio=decrease:force_divisible_by=2[v1080out]";

            // map + encode per-variant
            var maps = new StringBuilder();
            // Video bitrates/profiles tương ứng index
            var v = new (string label, string br, string profile)[]
            {
                ("[v144out]","300k","baseline"),
                ("[v240out]","700k","baseline"),
                ("[v360out]","1000k","main"),
                ("[v480out]","1500k","main"),
                ("[v720out]","2500k","main"),
                ("[v1080out]","4000k","high"),
            };

            for (int i = 0; i < v.Length; i++)
            {
                maps.Append(
                    $" -map \"{v[i].label}\" -c:v:{i} libx264 -b:v:{i} {v[i].br} -profile:v:{i} {v[i].profile} " +
                    $"-g 48 -keyint_min 48 -sc_threshold 0 -pix_fmt yuv420p"
                );

                if (hasAudio)
                {
                    // audio từ input (nếu có kênh), downmix stereo cho tương thích
                    maps.Append($" -map 0:a:0? -c:a:{i} aac -b:a:{i} 128k -ac 2 -ar 48000");
                }
            }

            // var_stream_map tương ứng
            var varStreamMap = hasAudio
                ? "\"v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3 v:4,a:4 v:5,a:5\""
                : "\"v:0 v:1 v:2 v:3 v:4 v:5\"";

            // HLS options (giữ output như cũ, thêm independent_segments cho keyframe boundary)
            var hls =
                $"-f hls -hls_time 6 -hls_list_size 0 -hls_flags independent_segments " +
                $"-var_stream_map {varStreamMap} " +
                $"-master_pl_name master.m3u8 " +
                $"-hls_segment_filename \"{outputFolder}/v%v/segment%d.ts\" " +
                $"\"{outputFolder}/v%v/playlist.m3u8\"";

            // Nếu hoàn toàn không có audio, loại phụ đề nếu có để tránh rắc rối
            var extra = hasAudio ? "" : " -sn";

            // max_muxing_queue_size để tránh lỗi queue với vài file lạ
            var safety = " -max_muxing_queue_size 1024";

            return
                $"-i \"{inputPath}\" -filter_complex \"{filterComplex}\"{maps}{safety}{extra} {hls}";
        }

        public FfmpegService(ILogger<FfmpegService> logger, IOptions<FfmpegSettings> settings, IMinioService minioService)
        {
            _logger = logger;
            _minioService = minioService;
            _tempFolder = Path.Combine(Path.GetTempPath(), "ffmpeg-tmp");
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

            return double.TryParse(output.Trim(), out double seconds)
                ? TimeSpan.FromSeconds(seconds)
                : null;
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
            Console.WriteLine($"FFmpeg stdout: {stdout}");
            Console.WriteLine($"FFmpeg stderr: {stderr}");

            await process.WaitForExitAsync();

            if (process.ExitCode != 0)
                throw new Exception($"FFmpeg exited with code {process.ExitCode}. Error: {stderr}");
        }

        public async Task<VideoProcessResultModel> ProcessVideoAsync(IFormFile videoFile)
        {
            if (videoFile == null || videoFile.Length == 0)
                throw new ArgumentException("Invalid video file.");
            if (videoFile.Length > 5L * 1024 * 1024 * 1024)
                throw new ArgumentException("Video file too large (limit: 5GB).");

            var fileId = Guid.NewGuid().ToString();
            var ext = Path.GetExtension(videoFile.FileName);
            var tempInputPath = Path.Combine(_tempFolder, $"{fileId}{ext}");
            var outputFolder = Path.Combine(_tempFolder, fileId);
            var thumbnailPath = Path.Combine(_tempFolder, $"{fileId}.jpg");
            var masterPlaylistPath = Path.Combine(outputFolder, "master.m3u8");

            Directory.CreateDirectory(outputFolder);
            Directory.CreateDirectory(Path.GetDirectoryName(thumbnailPath)!);

            try
            {
                // tạo thư mục variant
                for (int i = 0; i <= 5; i++)
                {
                    Directory.CreateDirectory(Path.Combine(outputFolder, $"v{i}"));
                }

                // Save input
                await using (var stream = new FileStream(tempInputPath, FileMode.Create))
                {
                    await videoFile.CopyToAsync(stream);
                }

                // Thumbnail
                var thumbnailArgs = $"-ss 00:00:01 -i \"{tempInputPath}\" -frames:v 1 -q:v 2 \"{thumbnailPath}\"";
                await RunFfmpegAsync(thumbnailArgs);

                // >>> PHÁT HIỆN AUDIO & BUILD LỆNH PHÙ HỢP <<<
                bool hasAudio = HasAudioStream(tempInputPath);
                var ffmpegArgs = BuildFfmpegArgs(tempInputPath, outputFolder, hasAudio);
                await RunFfmpegAsync(ffmpegArgs);

                // Upload thumbnail
                var thumbKey = $"thumbnails/{fileId}.jpg";
                await using (var thumbStream = File.OpenRead(thumbnailPath))
                    await _minioService.UploadStreamAsync(thumbKey, thumbStream, "image/jpeg");

                // Upload master playlist
                var baseVideoPath = $"videos/{fileId}/master.m3u8";
                await using (var masterStream = File.OpenRead(masterPlaylistPath))
                    await _minioService.UploadStreamAsync(baseVideoPath, masterStream, "application/x-mpegURL");

                // Upload từng playlist + segment
                for (int i = 0; i <= 5; i++)
                {
                    var segmentFolder = Path.Combine(outputFolder, $"v{i}");

                    var playlistFile = Path.Combine(segmentFolder, "playlist.m3u8");
                    if (File.Exists(playlistFile))
                    {
                        var playlistKey = $"videos/{fileId}/v{i}/playlist.m3u8";
                        await using var playlistStream = File.OpenRead(playlistFile);
                        await _minioService.UploadStreamAsync(playlistKey, playlistStream, "application/x-mpegURL");
                    }

                    foreach (var segmentFile in Directory.GetFiles(segmentFolder, "segment*.ts"))
                    {
                        var segKey = $"videos/{fileId}/v{i}/{Path.GetFileName(segmentFile)}";
                        await using var segStream = File.OpenRead(segmentFile);
                        await _minioService.UploadStreamAsync(segKey, segStream, "video/mp2t"); // <— sửa MIME
                    }
                }

                var duration = GetVideoDuration(tempInputPath);
                var thumbnailUrl = await _minioService.GetPublicFileUrlAsync(thumbKey);
                var hlsUrl = await _minioService.GetPublicFileUrlAsync(baseVideoPath);

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
                Console.WriteLine($"Video processing failed: {ex.Message}");
                return new VideoProcessResultModel
                {
                    Status = "failed"
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
