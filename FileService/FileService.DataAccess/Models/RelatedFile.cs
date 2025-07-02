using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson;

namespace FileService.DataAccess.Models
{
    public class RelatedFile : BaseDocument
    {
        public Guid UserId { get; set; }
        public List<Guid> FileIds { get; set; } = new();
    }
}
