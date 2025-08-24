using OrganizationService.Core.Constants;
using System;

namespace OrganizationService.Service.ApiModels.OrganizationMember
{
    public class AddMemberRequest
    {
        public Guid UserId { get; set; }
        public MemberRole Role { get; set; } = MemberRole.Student;
        public bool IsActive { get; set; } = true;
    }
}