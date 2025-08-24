using OrganizationService.Core.ApiModels;
using OrganizationService.DataAccess.Interfaces;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;

namespace OrganizationService.Api.Middlewares
{
    public class UserContextMiddleware
    {
        private readonly RequestDelegate _next;
        private readonly ILogger<UserContextMiddleware> _logger;

        public UserContextMiddleware(RequestDelegate next, ILogger<UserContextMiddleware> logger)
        {
            _next = next;
            _logger = logger;
        }

        public async Task InvokeAsync(HttpContext httpContext, AppSettings appSettings, UserContext userContext, IUnitOfWork unitOfWork)
        {
            // Lấy token từ header Authorization
            var token = httpContext.Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
            if (!string.IsNullOrEmpty(token))
            {
                userContext.Token = token;
                try
                {
                    var handler = new JwtSecurityTokenHandler();
                    var jwtToken = handler.ReadJwtToken(token);
                    var roles = string.Join(", ", jwtToken.Claims.Where(c => c.Type == "role" || c.Type == ClaimTypes.Role).Select(c => c.Value));
                    _logger.LogInformation($"UserContextMiddleware: Token: {token.Substring(0, Math.Min(10, token.Length))}..., Roles: {roles}");
                }
                catch (Exception ex)
                {
                    _logger.LogWarning($"UserContextMiddleware: Failed to parse JWT token: {ex.Message}");
                }
            }
            else
            {
                _logger.LogWarning("UserContextMiddleware: No Bearer token provided in Authorization header");
            }

            // Lấy claims từ httpContext.User
            var userId = httpContext.User.Claims.FirstOrDefault(c => c.Type == "userId")?.Value;
            var email = httpContext.User.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Email)?.Value;
            var sessionId = httpContext.User.Claims.FirstOrDefault(c => c.Type == "sessionId")?.Value;
            var role = httpContext.User.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Role)?.Value;
            var userType = httpContext.User.Claims.FirstOrDefault(c => c.Type == "userType")?.Value;

            if (userContext == null)
            {
                userContext = new UserContext();
            }

            if (!string.IsNullOrEmpty(email))
            {
                userContext.Email = email;
            }

            if (!string.IsNullOrEmpty(userId) && Guid.TryParse(userId, out Guid uid))
            {
                userContext.UserId = uid;
            }

            if (!string.IsNullOrEmpty(sessionId) && Guid.TryParse(sessionId, out Guid sid))
            {
                userContext.SessionId = sid;
            }

            if (!string.IsNullOrEmpty(role))
            {
                userContext.Role = role;
            }
            _logger.LogInformation($"UserContextMiddleware: UserId: {userContext.UserId}, Email: {userContext.Email}, Role: {userContext.Role}, SessionId: {userContext.SessionId}");

            await _next(httpContext);
        }
    }
}
