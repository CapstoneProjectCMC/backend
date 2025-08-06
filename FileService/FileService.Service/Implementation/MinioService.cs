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
using Minio.Exceptions;


namespace FileService.Service.Implementation
{
    public class MinioService : BaseService, IMinioService
    {
        private readonly IMinioClient _minioClient;
        private readonly MinioConfig _config;

        public MinioService(AppSettings appSettings, UserContext userContext) : base(appSettings, userContext)
        {
            _config = appSettings.MinioConfig ?? throw new ArgumentNullException(nameof(appSettings.MinioConfig)); 

            _minioClient = new MinioClient()
                .WithEndpoint(_config.Endpoint, _config.Port)
                .WithCredentials(_config.AccessKey, _config.SecretKey)
                .WithSSL(_config.Secure)
                .Build();
        }

        public async Task EnsureBucketExistsAsync()
        {
            var bucketExistsArgs = new BucketExistsArgs().WithBucket(_config.BucketName);
            if (string.IsNullOrWhiteSpace(_config.BucketName))
            {
                throw new InvalidOperationException("Bucket name is not configured.");
            }
            bool found = await _minioClient.BucketExistsAsync(bucketExistsArgs);
            if (!found)
            {
                var makeBucketArgs = new MakeBucketArgs().WithBucket(_config.BucketName);
                await _minioClient.MakeBucketAsync(makeBucketArgs);
                Console.WriteLine($"Created bucket: {_config.BucketName}");
            }

            string bucketName = _config.BucketName;
            var getPolicyArgs = new GetPolicyArgs().WithBucket(bucketName);
            string existingPolicy = null;
            try
            {
                existingPolicy = await _minioClient.GetPolicyAsync(getPolicyArgs);
                Console.WriteLine($"Current bucket policy: {existingPolicy}");
            }
            catch (MinioException ex)
            {
                Console.WriteLine($"No policy found or error: {ex.Message}");
            }

            if (existingPolicy != GetPublicDownloadPolicy(bucketName))
            {
                var setPolicyArgs = new SetPolicyArgs()
                    .WithBucket(bucketName)
                    .WithPolicy(GetPublicDownloadPolicy(bucketName));
                try
                {
                    await _minioClient.SetPolicyAsync(setPolicyArgs);
                    Console.WriteLine($"Applied public policy to bucket: {bucketName}");
                }
                catch (MinioException ex)
                {
                    throw new Exception($"Failed to set bucket policy: {ex.Message}", ex);
                }
            }
        }

        private string GetPublicDownloadPolicy(string bucketName)
        {
            return $@"
            {{
                ""Version"": ""2012-10-17"",
                ""Statement"": [
                  {{
                      ""Effect"": ""Allow"",
                      ""Principal"": {{""AWS"": ""*""}},
                      ""Action"": [""s3:GetObject""],
                      ""Resource"": [""arn:aws:s3:::{bucketName}/*""]
                  }}
                ]
            }}";
        }

        public async Task UploadStreamAsync(string objectName, Stream fileStream, string contentType)
        {
            if (fileStream.Length == 0)
                throw new InvalidOperationException("Stream is empty");

            await EnsureBucketExistsAsync();

            var args = new PutObjectArgs()
                .WithBucket(_config.BucketName)
                .WithObject(objectName)
                .WithStreamData(fileStream)
                .WithObjectSize(fileStream.Length)
                .WithContentType(contentType);
            await _minioClient.PutObjectAsync(args);
        }
        public Task<string> GetPublicFileUrlAsync(string objectName)
        {
            if (string.IsNullOrWhiteSpace(objectName) || objectName.Any(c => "!@#$%^&*()".Contains(c)))
            {
                throw new ArgumentException("Invalid object name", nameof(objectName));
            }

            var url = $"{(_config.Secure ? "https" : "http")}://{_config.Endpoint}:{_config.Port}/{_config.BucketName}/{objectName}";
            return Task.FromResult(url);
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
                .WithBucket(_config.BucketName)
                .WithObject(objectName);
            await _minioClient.RemoveObjectAsync(args);
        }
    }
}
