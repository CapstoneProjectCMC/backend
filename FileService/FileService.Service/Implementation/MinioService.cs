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

        public MinioService(IOptions<MinioConfig> options, AppSettings appSettings, UserContext userContext) : base(appSettings, userContext)
        {
            _config = options.Value ?? throw new ArgumentNullException(nameof(options.Value));

            Console.WriteLine($"MinioConfig: Endpoint={_config.Endpoint}, PublicEndpoint={_config.PublicEndpoint}, Port={_config.Port}, AccessKey length={_config.AccessKey?.Length ?? 0}, Secure={_config.Secure}");

            if (string.IsNullOrWhiteSpace(_config.Endpoint) || _config.Port <= 0 || string.IsNullOrWhiteSpace(_config.AccessKey) || string.IsNullOrWhiteSpace(_config.SecretKey))
            {
                throw new InvalidOperationException("Minio configuration is invalid or incomplete.");
            }
            try
            {
                Console.WriteLine("Attempting to build MinioClient...");
                _minioClient = new MinioClient()
                    .WithEndpoint(_config.Endpoint, _config.Port)
                    .WithCredentials(_config.AccessKey, _config.SecretKey)
                    .WithSSL(_config.Secure)
                    .Build();
                if (_minioClient == null)
                {
                    throw new InvalidOperationException("Failed to initialize MinioClient.");
                }
                // Kiểm tra kết nối thực tế
                Console.WriteLine("Testing MinioClient connection...");
                var buckets = _minioClient.ListBucketsAsync().GetAwaiter().GetResult(); // Kiểm tra nhanh
                Console.WriteLine("MinioClient initialized and connection tested successfully.");
            }
            catch (MinioException ex)
            {
                Console.WriteLine($"MinioException: {ex.Message}");
                throw new InvalidOperationException($"Minio initialization failed: {ex.Message}", ex);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Unexpected error: {ex.Message}");
                throw new InvalidOperationException($"Failed to initialize MinioClient: {ex.Message}", ex);
            }
        }

        public async Task EnsureBucketExistsAsync()
        {
            if (_minioClient == null)
            {
                throw new InvalidOperationException("MinioClient is not initialized.");
            }

            if (string.IsNullOrWhiteSpace(_config.BucketName))
            {
                throw new InvalidOperationException("Bucket name is not configured.");
            }
            var bucketExistsArgs = new BucketExistsArgs().WithBucket(_config.BucketName);

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
            if (string.IsNullOrWhiteSpace(objectName))
                throw new ArgumentException("Invalid object name", nameof(objectName));

            // 1) Chuẩn hoá key và chỉ encode từng segment, không encode dấu '/'
            var key = string.Join("/",
                objectName.Trim().TrimStart('/')
                          .Split('/', StringSplitOptions.RemoveEmptyEntries)
                          .Select(s => Uri.EscapeDataString(s))
            );

            // 2) Nếu có PublicEndpoint (vd: https://codecampus.site/s3) thì dùng làm base
            var pub = _config.PublicEndpoint?.Trim().TrimEnd('/');
            if (!string.IsNullOrEmpty(pub))
            {
                if (!pub.StartsWith("http://", StringComparison.OrdinalIgnoreCase) &&
                    !pub.StartsWith("https://", StringComparison.OrdinalIgnoreCase))
                {
                    pub = $"{(_config.Secure ? "https" : "http")}://{pub}";
                }
                return Task.FromResult($"{pub}/{_config.BucketName}/{key}");
            }

            // 3) Fallback: endpoint nội bộ MinIO
            var scheme = _config.Secure ? "https" : "http";
            return Task.FromResult($"{scheme}://{_config.Endpoint}:{_config.Port}/{_config.BucketName}/{key}");
        }

        // Thêm tiện ích: nhận vào URL hoặc key, trả về object key dùng cho MinIO
        public string UrlToObjectKey(string urlOrKey)
        {
            if (string.IsNullOrWhiteSpace(urlOrKey)) return urlOrKey;

            var s = urlOrKey.Trim();

            // Nếu đã là key thì trả về key (bỏ dấu '/')
            if (!s.StartsWith("http://", StringComparison.OrdinalIgnoreCase) &&
                !s.StartsWith("https://", StringComparison.OrdinalIgnoreCase))
                return s.TrimStart('/');

            // Cắt bỏ PublicEndpoint (nếu khớp)
            var pub = _config.PublicEndpoint?.Trim().TrimEnd('/') ?? "";
            if (!string.IsNullOrEmpty(pub) && s.StartsWith(pub, StringComparison.OrdinalIgnoreCase))
                s = s.Substring(pub.Length);

            s = s.TrimStart('/');

            // Cắt bỏ bucket name nếu có
            var bucketPrefix = _config.BucketName + "/";
            if (s.StartsWith(bucketPrefix, StringComparison.OrdinalIgnoreCase))
                s = s.Substring(bucketPrefix.Length);

            // Giải mã phần còn lại (đề phòng URL cũ có %2F)
            return Uri.UnescapeDataString(s);
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
