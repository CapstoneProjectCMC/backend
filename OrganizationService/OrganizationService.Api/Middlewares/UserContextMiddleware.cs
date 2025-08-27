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
            var role = httpContext.User.Claims.Where(c => c.Type == ClaimTypes.Role).Select(c => c.Value).ToList();
            var userType = httpContext.User.Claims.FirstOrDefault(c => c.Type == "userType")?.Value;
            var username = httpContext.User.Claims.FirstOrDefault(c => c.Type == "username")?.Value;
            var permissions = httpContext.User.Claims.Where(c => c.Type == "permissions").Select(c => c.Value).ToList();
            var organizationId = httpContext.User.Claims.FirstOrDefault(c => c.Type == "org_id")?.Value;
            var organizationRole = httpContext.User.Claims.FirstOrDefault(c => c.Type == "org_role")?.Value;
            var scope = httpContext.User.Claims.FirstOrDefault(c => c.Type == "scope")?.Value;


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

            if (role != null && role.Any())
            {
                userContext.Roles = role;
            }

            if (!string.IsNullOrEmpty(username))
            {
                userContext.Username = username;
            }

            if (permissions != null && permissions.Any())
            {
                userContext.Permissions = permissions;
            }

            await _next(httpContext);
        }
    }
}
