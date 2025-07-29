using FileService.Core.ApiModels;
using FileService.DataAccess.Interfaces;
using FileService.DataAccess.Models;
using FileService.Service.Interfaces;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Minio;
using Minio.DataModel.Args;
using MinioConfig = FileService.Core.ApiModels.MinioConfig;


namespace FileService.Service.Implementation
{
    public class MinioService : BaseService, IMinioService
    {
        private readonly UserContext _userContext;
        private readonly IMinioClient _minioClient;
        private readonly MinioConfig _config;
        private readonly string BucketName;

        public MinioService(
            AppSettings appSettings,
            UserContext userContext
            )
             : base(appSettings, userContext)
        {

            _userContext = userContext;

            _config = appSettings.MinioConfig ?? throw new ArgumentNullException(nameof(appSettings.MinioConfig)); 

            _minioClient = new MinioClient()
                .WithEndpoint(_config.Endpoint, _config.Port)
                .WithCredentials(_config.AccessKey, _config.SecretKey)
                .WithSSL(_config.Secure)
                .Build();
            BucketName = _config.BucketName;
        }
        public async Task EnsureBucketExistsAsync()
        {
            var bucketExistsArgs = new BucketExistsArgs().WithBucket(_config.BucketName);
            bool found = await _minioClient.BucketExistsAsync(bucketExistsArgs);
            if (!found)
            {
                var makeBucketArgs = new MakeBucketArgs().WithBucket(_config.BucketName);
                await _minioClient.MakeBucketAsync(makeBucketArgs);
            }
        }
        public async Task UploadFileAsync(string objectName, string filePath, string contentType)
        {
            await EnsureBucketExistsAsync();
            using (var stream = new FileStream(filePath, FileMode.Open, FileAccess.Read))
            {
                var args = new PutObjectArgs()
                    .WithBucket(BucketName)
                    .WithObject(objectName)
                    .WithStreamData(stream)
                    .WithContentType(contentType);
                await _minioClient.PutObjectAsync(args);
            }
        }
        public async Task<string> GetFileAsync(string objectName)
        {
            var presignedGetArgs = new PresignedGetObjectArgs()
                .WithBucket(_config.BucketName)
                .WithObject(objectName)
                .WithExpiry(7200); // Hết hạn sau 2 giờ
            return await _minioClient.PresignedGetObjectAsync(presignedGetArgs);
        }

        public async Task<string> GeneratePresignedUrlAsync(string objectName, int expirySeconds)
        {
            var presignedGetArgs = new PresignedGetObjectArgs()
                .WithBucket(_config.BucketName)
                .WithObject(objectName)
                .WithExpiry(expirySeconds);
            return await _minioClient.PresignedGetObjectAsync(presignedGetArgs);
        }

        public async Task RemoveFileAsync(string objectName)
        {
            var args = new RemoveObjectArgs()
                .WithBucket(BucketName)
                .WithObject(objectName);
            await _minioClient.RemoveObjectAsync(args);
        }
    }
}
