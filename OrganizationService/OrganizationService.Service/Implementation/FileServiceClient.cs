using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using Newtonsoft.Json.Linq;
using OrganizationService.Core.ApiModels;
using OrganizationService.Core.Enums;
using OrganizationService.Core.Exceptions;
using OrganizationService.Service.ApiModels.Organization;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Net.Http.Headers;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace OrganizationService.Service.Implementation
{
    public class FileServiceClient
    {
        private readonly HttpClient _httpClient;
        private readonly string _fileServiceBaseUrl;
        private readonly UserContext _userContext;
        private readonly ILogger<FileServiceClient> _logger;

        public FileServiceClient(HttpClient httpClient, string fileServiceBaseUrl, UserContext userContext, ILogger<FileServiceClient> logger)
        {
            _httpClient = httpClient ?? throw new ArgumentNullException(nameof(httpClient));
            _fileServiceBaseUrl = fileServiceBaseUrl?.TrimEnd('/') ?? throw new ArgumentNullException(nameof(fileServiceBaseUrl));
            _userContext = userContext ?? throw new ArgumentNullException(nameof(userContext));
            _logger = logger ?? throw new ArgumentNullException(nameof(logger));
            _logger.LogInformation($"FileServiceClient initialized with BaseUrl: {_fileServiceBaseUrl}");
            _logger.LogInformation($"UserContext: UserId: {_userContext.UserId}, Role: {_userContext.Role}, Token: {(string.IsNullOrEmpty(_userContext.Token) ? "null" : _userContext.Token.Substring(0, Math.Min(10, _userContext.Token.Length)) + "...")}");
        }

        private void AddAuthHeader()
        {
            // Kiểm tra token và roles
            if (string.IsNullOrEmpty(_userContext.Token))
            {
                _logger.LogError("UploadLogoAsync: No Bearer token provided. File Service requires authentication.");
                throw new ErrorException(StatusCodeEnum.A02, "No authentication token provided for File Service.");
            }

            // Log roles từ token
            string roles = _userContext.Role ?? "none";
            if (!string.IsNullOrEmpty(_userContext.Token))
            {
                try
                {
                    var handler = new JwtSecurityTokenHandler();
                    var token = handler.ReadJwtToken(_userContext.Token);
                    roles = string.Join(", ", token.Claims.Where(c => c.Type == "role" || c.Type == ClaimTypes.Role).Select(c => c.Value));
                }
                catch (Exception ex)
                {
                    _logger.LogWarning($"UploadLogoAsync: Failed to parse JWT token: {ex.Message}");
                }
            }
            _logger.LogInformation($"UploadLogoAsync: Using Bearer token: {_userContext.Token.Substring(0, Math.Min(10, _userContext.Token.Length))}..., Roles: {roles}");

            // Thêm Bearer token
            _httpClient.DefaultRequestHeaders.Authorization =
                new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", _userContext.Token);

        }

        public async Task<FileUploadResult> UploadLogoAsync(IFormFile logoFile, Guid orgId)
        {
            //kiểm tra file
            if (logoFile == null || logoFile.Length == 0)
                throw new ErrorException(Core.Enums.StatusCodeEnum.A01, "Logo file is empty or null");

            // Kiểm tra định dạng file
            var validExtensions = new[] { ".png", ".jpg", ".jpeg" };
            if (!validExtensions.Contains(Path.GetExtension(logoFile.FileName).ToLower()))
                throw new ErrorException(Core.Enums.StatusCodeEnum.A01, "Invalid file format. Only PNG, JPG, JPEG are allowed.");

            if (logoFile.Length > 5 * 1024 * 1024) // 5MB
            {
                throw new ErrorException(StatusCodeEnum.A01, "Logo file is too large.");
            }

            //kiểm tra Token
            AddAuthHeader();

            //tạo form data
            using var form = new MultipartFormDataContent();
            using var stream = logoFile.OpenReadStream();
            var streamContent = new StreamContent(stream);
            streamContent.Headers.ContentType = new System.Net.Http.Headers.MediaTypeHeaderValue(logoFile.ContentType);
            
            form.Add(streamContent, "file", logoFile.FileName);
            form.Add(new StringContent(orgId.ToString()), "OrgId");
            form.Add(new StringContent("Logo của các tổ chức"), "Description");
            form.Add(new StringContent(((int)FileCategory.Image).ToString()), "Category");
            form.Add(new StringContent("false"), "IsLectureVideo");
            form.Add(new StringContent("false"), "IsTextbook");
            form.Add(new StringContent("#logo"), "Tags");

            //call API
            var response = await _httpClient.PostAsync($"{_fileServiceBaseUrl}/file/api/FileDocument/add", form);

            if (!response.IsSuccessStatusCode)
            {
                var errorContent = await response.Content.ReadAsStringAsync();
                _logger.LogError($"UploadLogoAsync failed: {response.StatusCode}, Content: {errorContent}");
                throw new ErrorException(StatusCodeEnum.A01, $"UploadLogoAsync failed: {response.StatusCode}, Content: {errorContent}");
            }

            var json = await response.Content.ReadAsStringAsync();
            _logger.LogInformation($"UploadLogoAsync: Response content = {json}");

            var parsed = JObject.Parse(json);

            var fileId = parsed["result"]?["id"]?.ToString();
            var url = parsed["result"]?["url"]?.ToString();

            _logger.LogInformation($"Parsed fileId: {fileId}, url: {url}");

            if (string.IsNullOrEmpty(fileId) || string.IsNullOrEmpty(url))
            {
                throw new Exception($"File Service did not return a valid result. Raw response: {json}");
            }

            return new FileUploadResult
            {
                FileId = Guid.Parse(fileId),
                Url = url
            };

        }

        public async Task<FileUploadResult> UpdateLogoAsync(Guid? orgId, Guid fileId, IFormFile logo)
        {
            //kiểm tra Token
            AddAuthHeader();

            using var content = new MultipartFormDataContent();
            using var stream = logo.OpenReadStream();
            var fileContent = new StreamContent(stream);
            fileContent.Headers.ContentType = new MediaTypeHeaderValue(logo.ContentType);
            content.Add(fileContent, "file", logo.FileName);

            content.Add(new StringContent(logo.FileName), "FileName");
            content.Add(new StringContent("Logo của tổ chức"), "Description");
            content.Add(new StringContent(((int)FileCategory.Image).ToString()), "Category");
            content.Add(new StringContent("true"), "IsActive");
            content.Add(new StringContent("false"), "IsLectureVideo");
            content.Add(new StringContent("false"), "IsTextbook");
            content.Add(new StringContent(orgId.ToString() ?? ""), "OrgId");
            content.Add(new StringContent("#logo"), "Tags");

            //call API
            var response = await _httpClient.PutAsync($"{_fileServiceBaseUrl}/file/api/FileDocument/edit/{fileId}", content);

            var json = await response.Content.ReadAsStringAsync();
            _logger.LogInformation($"UploadLogoAsync: Response content = {json}");

            if (!response.IsSuccessStatusCode)
            {
                _logger.LogError($"UpdateLogoAsync failed: {response.StatusCode}, Content: {json}");
                throw new ErrorException(StatusCodeEnum.A01, $"UpdateLogoAsync failed: {response.StatusCode}, Content: {json}");
            }

            var parsed = JObject.Parse(json);
            var fileIdStr = parsed["result"]?["id"]?.ToString();
            var url = parsed["result"]?["url"]?.ToString();


            if (string.IsNullOrEmpty(fileIdStr) || string.IsNullOrEmpty(url))
                throw new Exception($"File Service did not return a valid result. Raw response: {json}");

            return new FileUploadResult
            {
                FileId = Guid.Parse(fileIdStr),
                Url = url
            };
        }

        public async Task DeleteFileAsync(Guid fileId)
        {
            //kiểm tra Token
            AddAuthHeader();

            // API xóa file từ FileService
            var response = await _httpClient.DeleteAsync($"{_fileServiceBaseUrl}/file/api/FileDocument/{fileId}");
            if (!response.IsSuccessStatusCode)
            {
                var json = await response.Content.ReadAsStringAsync();
                _logger.LogError($"DeleteFileAsync failed: {response.StatusCode}, Content: {json}");
                throw new ErrorException(StatusCodeEnum.A01, $"Failed to delete file from File Service. {json}");
            }
        }
    }
}
