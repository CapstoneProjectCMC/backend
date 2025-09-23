using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileService.Service.Interfaces
{
    public interface IMinioService
    {
        Task UploadStreamAsync(string objectName, Stream fileStream, string contentType);
        Task<string> GetPublicFileUrlAsync(string objectName);
        Task<string> GeneratePresignedUrlAsync(string objectName, int expirySeconds);
        Task EnsureBucketExistsAsync();
        Task RemoveFileAsync(string objectName);
        string UrlToObjectKey(string urlOrKey);
    }
}
