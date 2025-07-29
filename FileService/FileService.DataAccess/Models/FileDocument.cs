using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;
using FileService.Core.Enums;

namespace FileService.DataAccess.Models
{
    public class FileDocument : BaseDocument
    {
        public string FileName { get; set; }
        public string FileType { get; set; } // video/mp4, application/pdf, image/jpeg
        public long Size { get; set; } //bytes, MB, GB
        public string Url { get; set; } // http://localhost:<port>/static/{fileId}
        public string Checksum { get; set; } // MD5 hoặc SHA256 checksum của file để kiểm tra tính toàn vẹn

        [BsonRepresentation(BsonType.String)]
        public FileCategory Category { get; set; } // bài giảng, giáo trình, file thường
        public bool IsActive { get; set; } = true;
        public bool IsRemoved { get; set; } = false;
        public string Description { get; set; }
        public string ThumbnailUrl { get; set; } // cho video, hình ảnh
        public string TranscodingStatus { get; set; } // running, finished, failed
        public List<string> AssociatedResourceIds { get; set; } = new();
        public List<Guid> TagIds { get; set; } = new(); // references to Tags class

        // field dùng để xác định loại nội dung
        public bool IsLectureVideo { get; set; }
        public bool IsTextbook { get; set; }

        [BsonIgnoreIfDefault]
        public TimeSpan? Duration { get; set; } // video

        // fields chỉ áp dụng với video bài giảng hoặc giáo trình 
        [BsonIgnoreIfDefault]
        public int? ViewCount { get; set; }

        [BsonIgnoreIfDefault]
        public double? Rating { get; set; }

        // field dùng để phân loại theo tổ chức hoặc trường học, nếu là video bài giảng hoặc giáo trình
        public Guid? OrgId { get; set; }

        // đường dẫn .m3u8 cho video HLS
        public string? HlsUrl { get; set; }
    }
}
