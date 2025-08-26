using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Core.ApiModels
{
    public class UserContext
    {
        public Guid UserId { get; set; }
        public Guid SessionId { get; set; }
        public string? Email { get; set; }
        public string? Role { get; set; }
        public string? Username { get; set; }
        public Guid? ActionId { get; set; }
        public string? OrganizationId { get; set; }
        public string Token { get; set; } = string.Empty;
        public List<string> Permissions { get; set; } = new();
    }
}
