
namespace FileService.Service.ApiModels.FileDocumentModels
{
    public class VideoProcessResultModel
    {
        public string ThumbnailUrl { get; set; }
        public TimeSpan? Duration { get; set; }
        public string Status { get; set; } // finished, failed
    }
}
