using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileService.Service.ApiModels.FileDocumentModels
{
    public class VideoUploadResultModel
    {
        public string FileId { get; set; } = default!;
        public string OriginalUrl { get; set; } = default!;
        public string Status { get; set; } = "processing"; // hoặc "done", "failed"
    }
}
