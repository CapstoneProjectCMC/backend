using FileService.Core.ApiModels;
using FileService.Core.Enums;
using Microsoft.AspNetCore.Authorization;
using Microsoft.Extensions.Caching.Memory;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace FileService.Api.Middlewares
{
    public class RoleCheckMiddleware
    {
        private readonly RequestDelegate _next;
        private readonly ILogger<RoleCheckMiddleware> _logger;
        private readonly IMemoryCache _cache;

        public RoleCheckMiddleware(RequestDelegate next, ILogger<RoleCheckMiddleware> logger, IMemoryCache cache)
        {
            _next = next;
            _logger = logger;
            _cache = cache;
        }

        public async Task InvokeAsync(HttpContext context)
        {
            // Bỏ qua middleware nếu endpoint có [AllowAnonymous]
            var endpoint = context.GetEndpoint();
            if (endpoint?.Metadata?.GetMetadata<IAllowAnonymous>() != null)
            {
                _logger.LogDebug("Bypassing RoleCheckMiddleware for anonymous endpoint: {Path}", context.Request.Path);
                await _next(context);
                return;
            }

            // Lấy token từ header
            var token = context.Request.Headers["Authorization"].FirstOrDefault()?.Split(" ").Last();
            if (string.IsNullOrEmpty(token))
            {
                _logger.LogWarning("Missing token in request to {Path}", context.Request.Path);
                await WriteError(context, StatusCodeEnum.Unauthorized, "Missing token");
                return;
            }

            try
            {
                // Kiểm tra cache trước khi decode token
                var cacheKey = $"claims_{token}";
                List<Claim> claims;
                if (!_cache.TryGetValue(cacheKey, out claims))
                {
                    var tokenHandler = new JwtSecurityTokenHandler();
                    var jwtToken = tokenHandler.ReadJwtToken(token);

                    var roles = jwtToken.Claims
                        .Where(c => c.Type == "role" || c.Type == "roles")
                        .Select(c => c.Value)
                        .ToList();
                    _logger.LogInformation("Roles extracted from token: {Roles}", string.Join(", ", roles));

                    if (!roles.Any())
                    {
                        await WriteError(context, StatusCodeEnum.Forbidden, "No roles found in token");
                        return;
                    }
                    claims = jwtToken.Claims.ToList();
                    _cache.Set(cacheKey, claims, TimeSpan.FromMinutes(5));
                }
                else
                {
                    // Check roles again from cached claims
                    var roles = claims.Where(c => c.Type == "role" || c.Type == "roles").Select(c => c.Value).ToList();
                    _logger.LogInformation("Roles extracted from cached claims: {Roles}", string.Join(", ", roles));
                    if (!roles.Any())
                    {
                        _logger.LogWarning("No roles found in cached token claims for request to {Path}", context.Request.Path);
                        await WriteError(context, StatusCodeEnum.Forbidden, "No roles found in cached token claims");
                        return;
                    }
                }

                // Gắn claims vào context.User
                context.User = new ClaimsPrincipal(new ClaimsIdentity(claims, "jwt"));
                _logger.LogInformation("Passing RoleCheckMiddleware successfully for {Path}", context.Request.Path);
               
                await _next(context);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error processing token for request to {Path}", context.Request.Path);
                await WriteError(context, StatusCodeEnum.Unauthorized, "Invalid token format");
            }
        }

        private async Task WriteError(HttpContext context, StatusCodeEnum status, string message)
        {
            context.Response.StatusCode = (int)status;
            context.Response.ContentType = "application/json";

            var response = new ApiResponseModel<object>(status)
            {
                Result = null,
                Message = message
            };

            await context.Response.WriteAsJsonAsync(response);
        }
    }

    public static class RoleCheckMiddlewareExtensions
    {
        public static IApplicationBuilder UseRoleCheck(this IApplicationBuilder builder)
        {
            return builder.UseMiddleware<RoleCheckMiddleware>();
        }
    }
}
