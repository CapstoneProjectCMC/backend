using System;
namespace OrganizationService.Service.ApiModels.OrganizationMember
{
    public class CreateOrganizationMemberV2Request
    {
        public Guid UserId { get; set; }
        public string ScopeType { get; set; } = "Organization"; // "Organization" | "Grade" | "Class"
        public Guid ScopeId { get; set; }
        public string Role { get; set; } = "Student";           // "SuperAdmin" | "Admin" | "Teacher" | "Student" (chấp nhận ADMIN/TEACHER/STUDENT)
        public bool IsActive { get; set; } = true;
    }
}