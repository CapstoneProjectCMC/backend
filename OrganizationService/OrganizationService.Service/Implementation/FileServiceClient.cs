using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using OrganizationService.Core.ApiModels;
using OrganizationService.Core.Enums;
using OrganizationService.Core.Exceptions;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
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
            _httpClient = httpClient;
            _fileServiceBaseUrl = fileServiceBaseUrl.TrimEnd('/');
            _httpClient = httpClient ?? throw new ArgumentNullException(nameof(httpClient));
            _fileServiceBaseUrl = fileServiceBaseUrl?.TrimEnd('/') ?? throw new ArgumentNullException(nameof(fileServiceBaseUrl));
            _userContext = userContext ?? throw new ArgumentNullException(nameof(userContext));
            _logger = logger ?? throw new ArgumentNullException(nameof(logger));
            _logger.LogInformation($"FileServiceClient initialized with BaseUrl: {_fileServiceBaseUrl}");
            _logger.LogInformation($"UserContext: UserId: {_userContext.UserId}, Role: {_userContext.Role}, Token: {(string.IsNullOrEmpty(_userContext.Token) ? "null" : _userContext.Token.Substring(0, Math.Min(10, _userContext.Token.Length)) + "...")}");
        }

        public async Task<string> UploadLogoAsync(IFormFile logoFile, Guid orgId)
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
            _httpClient.DefaultRequestHeaders.Authorization = new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", _userContext.Token);

            //tạo form data
            using var form = new MultipartFormDataContent();
            using var stream = logoFile.OpenReadStream();

            form.Add(new StreamContent(stream), "File", logoFile.FileName);
            form.Add(new StringContent(orgId.ToString()), "OrgId");
            form.Add(new StringContent("Logo description"), "Description");
            form.Add(new StringContent("Image"), "Category");
            form.Add(new StringContent("false"), "IsLectureVideo");
            form.Add(new StringContent("false"), "IsTextbook");

            
            var response = await _httpClient.PostAsync($"{_fileServiceBaseUrl}/file/api/FileDocument/add", form);

            if (!response.IsSuccessStatusCode)
            {
                var errorContent = await response.Content.ReadAsStringAsync();
                try
                {
                    var error = JsonSerializer.Deserialize<ErrorResponse>(errorContent);
                    throw new ErrorException(error.StatusCode, error.Message);
                }
                catch
                {
                    throw new Exception($"Lỗi upload logo: {response.StatusCode} - {errorContent}");
                }
            }
          //  response.EnsureSuccessStatusCode();

            var json = await response.Content.ReadAsStringAsync();

            var result = JsonSerializer.Deserialize<ApiResponse>(json);
            if (string.IsNullOrEmpty(result?.Url))
                throw new Exception("File Service returned an empty URL.");

            return result.Url;
        }
    }

    public class ApiResponse
    {
        public string Url { get; set; }
    }

    public class ErrorResponse
    {
        public Core.Enums.StatusCodeEnum StatusCode { get; set; }
        public string Message { get; set; }
    }
}
