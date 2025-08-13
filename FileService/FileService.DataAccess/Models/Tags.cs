using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileService.DataAccess.Models
{
    public class Tags : BaseDocument
    {
        [BsonElement("label")]
        public string Label { get; set; } = string.Empty; // Ví dụ: "#java", "#code"
    }
}
