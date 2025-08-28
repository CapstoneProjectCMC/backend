using Microsoft.AspNetCore.Http;
using OrganizationService.Core.Constants;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.Dtos.Organization
{
    public class OrganizationDto
    {
        public Guid Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public string? Description { get; set; }
        public string? Address { get; set; }
        public string? Email { get; set; }
        public string? Phone { get; set; }
        public Guid? LogoId { get; set; }
        public string? LogoUrl { get; set; }
        public OrganizationStatus Status { get; set; }
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public Guid CreatedBy { get; set; }

        //vai trò của tổ chức
        public string? OrganizationRole { get; set; }
    }
}
