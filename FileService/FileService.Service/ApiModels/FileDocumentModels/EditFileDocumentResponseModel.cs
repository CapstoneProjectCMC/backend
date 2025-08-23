using FileService.Core.Enums;
using FileService.Service.ApiModels.TagModels;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileService.Service.ApiModels.FileDocumentModels
{
    public class EditFileDocumentResponseModel
    {
        public Guid Id { get; set; }
        public string FileName { get; set; }

        public string FileType { get; set; } // video/mp4, application/pdf, image/jpeg

        public long Size { get; set; } //bytes, MB, GB

        public string Url { get; set; } // http://localhost:<port>/static/{fileId}

        public string Checksum { get; set; } // SHA256 checksum của file để kiểm tra tính toàn vẹn

        public FileCategory Category { get; set; } // bài giảng, giáo trình, file thường

        public bool IsActive { get; set; } = true;

        public bool IsRemoved { get; set; } = false;

        public string Description { get; set; }

        public string ThumbnailUrl { get; set; } // cho video, hình ảnh

        public TimeSpan? Duration { get; set; } // video duration, nếu là video thì có giá trị
        public string? HlsUrl { get; set; } // đường dẫn .m3u8 cho video HLS

        public string TranscodingStatus { get; set; } // running, finished, failed

        public List<string> AssociatedResourceIds { get; set; } = new();

        // field dùng để xác định loại nội dung
        public bool IsLectureVideo { get; set; }

        public bool IsTextbook { get; set; }

        public List<TagModel> Tags { get; set; } 

        // fields chỉ áp dụng với video bài giảng hoặc giáo trình 
        public int? ViewCount { get; set; }
        public double? Rating { get; set; }

        // field dùng để phân loại theo tổ chức hoặc trường học, nếu là video bài giảng hoặc giáo trình
        public Guid? OrgId { get; set; }
        public DateTime? UpdatedAt { get; set; } = DateTime.UtcNow;
        public Guid? UpdatedBy { get; set; }
    }
}
