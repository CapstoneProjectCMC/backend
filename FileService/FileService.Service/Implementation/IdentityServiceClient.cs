using FileService.Core.ApiModels;
using FileService.DataAccess.Interfaces;
using FileService.DataAccess.Models;
using FileService.Service.ApiModels.UserModels;
using FileService.Service.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http.Json;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace FileService.Service.Implementation
{
    public class IdentityServiceClient : BaseService, IIdentityServiceClient
    {
        private readonly HttpClient _httpClient;
        private readonly IRepository<FileDocument> _fileDocumentRepository;
        private readonly UserContext _userContext;
        private readonly IFfmpegService _ffmpegService;
        private readonly IMinioService _minioService;
        private readonly ITagService _tagService;
        private readonly IRepository<Tags> _tagRepository;

        public IdentityServiceClient(
            AppSettings appSettings,
            UserContext userContext,
            IFfmpegService ffmpegService,
            IMinioService minioService,
            ITagService tagService,
            IRepository<FileDocument> fileDocumentRepository,
            IRepository<Tags> tagRepository,
            HttpClient httpClient
            )
             : base(appSettings, userContext)
        {
            _fileDocumentRepository = fileDocumentRepository;
            _userContext = userContext;
            _ffmpegService = ffmpegService;
            _minioService = minioService;
            _tagService = tagService;
            _tagRepository = tagRepository;
            _httpClient = httpClient;
        }

        public async Task<UserProfileResponse?> GetUserProfileAsync(string userId)
        {
            var response = await _httpClient.GetAsync($"http://localhost:8081/profile/internal/user/{userId}");
            if (!response.IsSuccessStatusCode)
            {
                return new UserProfileResponse
                {
                    UserId = userId,
                    Username = "",
                    Email = "",
                    DisplayName = "",
                    AvatarUrl = "",
                    Active = false,
                    Roles = new List<string> { "" }
                };
            }

            var json = await response.Content.ReadAsStringAsync();
            return System.Text.Json.JsonSerializer.Deserialize<UserProfileResponse>(
                json,
                new System.Text.Json.JsonSerializerOptions { PropertyNameCaseInsensitive = true }
            );
        }

        public async Task<List<UserProfileResponse>> GetAllUserProfilesAsync(int page, int size)
        {
            var response = await _httpClient.GetAsync($"http://localhost:8081/profile/users?page={page}&size={size}");
            if (!response.IsSuccessStatusCode)
            {
                
            }
            var json = await response.Content.ReadAsStringAsync();
            
            var result = JsonSerializer.Deserialize<List<UserProfileResponse>>(json,
                new JsonSerializerOptions { PropertyNameCaseInsensitive = true });
            
            return result;
        }
    }
}
