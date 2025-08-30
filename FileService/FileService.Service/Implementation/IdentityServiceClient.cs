using FileService.Core.ApiModels;
using FileService.Core.Enums;
using FileService.Core.Exceptions;
using FileService.DataAccess.Interfaces;
using FileService.DataAccess.Models;
using FileService.Service.ApiModels.UserModels;
using FileService.Service.Interfaces;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Caching.Memory;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Net.Http.Json;
using System.Security.Claims;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace FileService.Service.Implementation
{
    public class IdentityServiceClient : BaseService, IIdentityServiceClient
    {
        private readonly HttpClient _httpClient;
        private readonly UserContext _userContext;
        private readonly IMemoryCache _cache;
        private readonly ILogger<IdentityServiceClient> _logger;
        private readonly string _baseUrl;


        public IdentityServiceClient(
            AppSettings appSettings,
            UserContext userContext,
            HttpClient httpClient,
            IMemoryCache cache,
            ILogger<IdentityServiceClient> logger,
            IConfiguration configuration
            )
             : base(appSettings, userContext)
        {
            _userContext = userContext;
            _httpClient = httpClient;
            _cache = cache;
            _logger = logger;
            _baseUrl = configuration["Appsettings:ApiSettings:UserServiceBaseUrl"];
        }

        public async Task<UserProfileResponse?> GetUserProfileAsync(string userId)
        {
            if (_cache.TryGetValue(userId, out UserProfileResponse? cachedUser))
            {
                return cachedUser;
            }

            if (string.IsNullOrEmpty(_userContext.Token))
                throw new ErrorException(StatusCodeEnum.D02);

            _httpClient.DefaultRequestHeaders.Authorization =
                new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", _userContext.Token);

            var request = new HttpRequestMessage(HttpMethod.Get, $"{_baseUrl}/profile/internal/user/{userId}");
            var response = await _httpClient.SendAsync(request);
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
            var user = JsonSerializer.Deserialize<UserProfileResponse>(json,
            new JsonSerializerOptions { PropertyNameCaseInsensitive = true });
            if (user != null)
            {
                _cache.Set(userId, user, TimeSpan.FromMinutes(5)); // cache 5 phút
            }
            return user;
        }

        public async Task<List<UserProfileResponse>> GetAllUserProfilesAsync()
        {
            if (string.IsNullOrEmpty(_userContext.Token))
                throw new ErrorException(StatusCodeEnum.D02);

            // Thêm Bearer token
            _httpClient.DefaultRequestHeaders.Authorization =
                new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", _userContext.Token);

            int page = 1;
            int size = 10;
            var allUsers = new List<UserProfileResponse>();

            while (true)
            {
                var request = new HttpRequestMessage(HttpMethod.Get, $"{_baseUrl}/profile/users?page={page}&size={size}");
                var response = await _httpClient.SendAsync(request);

                if (!response.IsSuccessStatusCode)
                    break;

                var json = await response.Content.ReadAsStringAsync();
                var apiResponse = JsonSerializer.Deserialize<ApiResponseModel<PaginatedList<UserProfileResponse>>>(json,
                    new JsonSerializerOptions { PropertyNameCaseInsensitive = true });

                if (apiResponse?.Result == null)
                {
                    Console.WriteLine("Result is null");
                }
                else if (apiResponse.Result.Datas == null || !apiResponse.Result.Datas.Any())
                {
                    Console.WriteLine("Datas is null or empty");
                }

                if (apiResponse?.Result?.Datas == null || !apiResponse.Result.Datas.Any())
                    break;

                var users = apiResponse.Result.Datas.ToList();
                allUsers.AddRange(users);

                // Cache từng user
                foreach (var u in users)
                {
                    if (!string.IsNullOrEmpty(u.UserId))
                        _cache.Set(u.UserId, u, TimeSpan.FromMinutes(5));
                }

                if (users.Count < size)
                    break; // hết dữ liệu
                page++;
            }

            return allUsers;
        }
    }
}