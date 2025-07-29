using Microsoft.AspNetCore.Authorization;

namespace OrganizationService.Api.Middlewares
{
    public class OrganizationServiceAuthorizeAttribute : AuthorizeAttribute
    {
        public OrganizationServiceAuthorizeAttribute()
        {
        }
        public OrganizationServiceAuthorizeAttribute(params string[] roles)
        {
            Roles = string.Join(",", roles);
        }
    }
}
