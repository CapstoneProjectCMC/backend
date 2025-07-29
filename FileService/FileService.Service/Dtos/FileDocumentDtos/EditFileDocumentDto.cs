using FileService.Core.Enums;
using Microsoft.AspNetCore.Http;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileService.Service.Dtos.FileDocumentDtos
{
    public class EditFileDocumentDto
    {
        public IFormFile? File { get; set; } 
        public string FileName { get; set; }
        public FileCategory Category { get; set; } // bài giảng, giáo trình, file thường
        public string Description { get; set; }
        public List<string> Tags { get; set; } = new(); // #java, #code

        // field dùng để xác định loại nội dung
        public bool IsLectureVideo { get; set; }
        public bool IsTextbook { get; set; }
        public bool IsActive { get; set; } 
        // field dùng để phân loại theo tổ chức hoặc trường học, nếu là video bài giảng hoặc giáo trình
        public Guid? OrgId { get; set; }
    }
}
