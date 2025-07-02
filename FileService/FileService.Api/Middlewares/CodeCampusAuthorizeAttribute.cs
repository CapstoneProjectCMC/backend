using Microsoft.AspNetCore.Authorization;
using System.Net;

namespace FileService.Api.Middlewares
{
    public class CodeCampusAuthorizeAttribute : AuthorizeAttribute
    {
        public CodeCampusAuthorizeAttribute()
        {
        }
        public CodeCampusAuthorizeAttribute(params string[] roles)
        {
            Roles = string.Join(",", roles);
        }
    }
}
