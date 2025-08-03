using OrganizationService.DataAccess.Interfaces;

namespace OrganizationService.Api.Middlewares
{
    public class AuthenMiddleware
    {
        private readonly RequestDelegate _next;
        public AuthenMiddleware(RequestDelegate next)
        {
            _next = next;
        }
        public async Task InvokeAsync(HttpContext httpContext, IUnitOfWork unitOfWork)
        {
            var userId = httpContext.User.Claims.FirstOrDefault(c => c.Type == "userId")?.Value;
            var sessionId = httpContext.User.Claims.FirstOrDefault(c => c.Type == "sessionId")?.Value;

            await _next(httpContext);
        }
    }
}
