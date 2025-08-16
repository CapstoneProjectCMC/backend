using FileService.Core.ApiModels;
using System.Security.Claims;

namespace FileService.Api.Middlewares
{
    public class AuthenMiddleware
    {
        private readonly RequestDelegate _next;


        public AuthenMiddleware(RequestDelegate next)
        {
            _next = next;
        }
        public async Task InvokeAsync(HttpContext httpContext)
        {
            //lấy userId và sessionId từ claims
            var userId = httpContext.User.Claims.FirstOrDefault(c => c.Type == "userId")?.Value;
            var sessionId = httpContext.User.Claims.FirstOrDefault(c => c.Type == "sessionId")?.Value;

            await _next(httpContext);
        }
    }
}
