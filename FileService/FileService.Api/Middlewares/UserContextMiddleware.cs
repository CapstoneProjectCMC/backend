using FileService.Core.ApiModels;
using System.Security.Claims;

namespace FileService.Api.Middlewares
{
    public class UserContextMiddleware
    {
        private readonly RequestDelegate _next;
        public UserContextMiddleware(RequestDelegate next)
        {
            _next = next;
        }

        public async Task InvokeAsync(HttpContext httpContext, AppSettings appsettings, UserContext userContext)
        {
            var userId = httpContext.User.Claims.FirstOrDefault(c => c.Type == "userId")?.Value;
            var email = httpContext.User.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Email)?.Value;
            var sessionId = httpContext.User.Claims.FirstOrDefault(c => c.Type == "sessionId")?.Value;
            var roles = httpContext.User.Claims.Where(c => c.Type == ClaimTypes.Role).Select(c => c.Value).ToList();
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

            if (roles != null && roles.Any())
            {
                userContext.Roles = roles;
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
