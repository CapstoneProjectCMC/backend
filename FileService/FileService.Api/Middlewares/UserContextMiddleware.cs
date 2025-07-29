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
            var role = httpContext.User.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Role)?.Value;
            var userType = httpContext.User.Claims.FirstOrDefault(c => c.Type == "userType")?.Value;
            var username = httpContext.User.Claims.FirstOrDefault(c => c.Type == "username")?.Value;
            var permissions = httpContext.User.Claims.Where(c => c.Type == "permissions").Select(c => c.Value).ToList();


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
