using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;

namespace FileService.DataAccess.Models
{
    public class FileDocument : BaseDocument
    {
       // [BsonElement("fileName")]
        public string FileName { get; set; }

       // [BsonElement("fileType")]
        public string FileType { get; set; } // video/mp4, application/pdf, image/jpeg
        
     //   [BsonElement("size")]
        public long Size { get; set; } //bytes, MB, GB

      //  [BsonElement("url")]
        public string Url { get; set; } // http://localhost:<port>/static/{fileId}
        
      //  [BsonElement("checksum")]
        public string Checksum { get; set; } // MD5 hoặc SHA256 checksum của file để kiểm tra tính toàn vẹn

     //   [BsonElement("category")]
        public string Category { get; set; } // bài giảng, giáo trình, file thường

      //  [BsonElement("isActive")]
        public bool IsActive { get; set; } = true;

      //  [BsonElement("isRemoved")]
        public bool IsRemoved { get; set; } = false;

     //   [BsonElement("description")]
        public string Description { get; set; }

      //  [BsonElement("thumbnailUrl")]
        public string ThumbnailUrl { get; set; } // cho video, hình ảnh

     //   [BsonElement("transcodingStatus")]
        public string TranscodingStatus { get; set; } // running, finished, failed

      //  [BsonElement("associatedResourceIds")]
        public List<string> AssociatedResourceIds { get; set; } = new();

     //   [BsonElement("tags")]
        public List<string> Tags { get; set; } = new(); // #java, #code

        // field dùng để xác định loại nội dung
      //  [BsonElement("isLectureVideo")]
        public bool IsLectureVideo { get; set; }

     //   [BsonElement("isTextbook")]
        public bool IsTextbook { get; set; }

      //  [BsonElement("duration")]
        [BsonIgnoreIfDefault]
        public TimeSpan? Duration { get; set; } // video


        // fields chỉ áp dụng với video bài giảng hoặc giáo trình 
     //   [BsonElement("viewCount")]
        [BsonIgnoreIfDefault]
        public int? ViewCount { get; set; }

     //   [BsonElement("rating")]
        [BsonIgnoreIfDefault]
        public double? Rating { get; set; }

     //   [BsonElement("comments")]
        public List<string> Comments { get; set; } = new(); 

        // field dùng để phân loại theo tổ chức hoặc trường học, nếu là video bài giảng hoặc giáo trình
     //   [BsonElement("orgId")]
        public string? OrgId { get; set; }

    }
}
