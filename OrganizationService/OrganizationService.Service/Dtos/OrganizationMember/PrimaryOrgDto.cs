using OrganizationService.Core.Constants;

namespace OrganizationService.Service.Dtos.OrganizationMember
{
    public class PrimaryOrgDto
    {
        public Guid OrganizationId { get; set; }
        public MemberRole Role { get; set; }
    }
}