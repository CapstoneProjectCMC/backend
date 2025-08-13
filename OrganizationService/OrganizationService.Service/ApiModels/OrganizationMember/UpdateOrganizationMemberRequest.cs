using OrganizationService.Core.Constants;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.ApiModels.OrganizationMember
{
    public class UpdateOrganizationMemberRequest
    {
        public MemberRole Role { get; set; }
        public bool IsActive { get; set; }
    }
}
