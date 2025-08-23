using Microsoft.AspNetCore.Http;
using OrganizationService.Core.Constants;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.ApiModels.Organization
{
    public class CreateOrganizationRequest
    {
        public string Name { get; set; } = string.Empty;
        public string? Description { get; set; }
        public string? Address { get; set; }
        public string? Email { get; set; }
        public string? Phone { get; set; }
      //  public Guid? Logo { get; set; }
        public IFormFile? LogoUrl { get; set; }
        public OrganizationStatus Status { get; set; }
    }
}
