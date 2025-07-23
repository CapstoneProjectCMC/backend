using FileService.Core.Enums;
using Microsoft.AspNetCore.Http;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileService.Service.Dtos.FileDocumentDtos
{
    public class AddFileDocumentDto
    {
        [Required]
        public IFormFile File { get; set; }

        [BsonRepresentation(BsonType.String)]
        public FileCategory Category { get; set; } // bài giảng, giáo trình, file thường
        public string Description { get; set; }
        public List<string> Tags { get; set; } = new(); // #java, #code
        
        // field dùng để xác định loại nội dung
        public bool IsLectureVideo { get; set; } = false;
        public bool IsTextbook { get; set; } = false;
        public string? OrgId { get; set; }
    }
}
