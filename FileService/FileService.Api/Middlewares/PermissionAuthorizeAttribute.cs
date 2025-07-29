using Microsoft.AspNetCore.Authorization;

namespace FileService.Api.Middlewares
{
    public class PermissionAuthorizeAttribute : AuthorizeAttribute
    {
        public string[] Permissions { get; }

        public PermissionAuthorizeAttribute(params string[] permissions)
        {
            Permissions = permissions;
            Policy = string.Join(",", permissions); // để gán vào policy
        }
    }
}
