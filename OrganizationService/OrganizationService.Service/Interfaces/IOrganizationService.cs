using Microsoft.AspNetCore.Http;
using OrganizationService.Service.ApiModels.Organization;
using OrganizationService.Service.Dtos.Organization;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.Interfaces
{
    public interface IOrganizationService
    {
        Task<IEnumerable<OrganizationDto>> GetViewModelsAsync(OrganizationModel orgModel);
        Task<OrganizationDto> GetByIdAsync(Guid id);
        Task<OrganizationDto> CreateAsync(CreateOrganizationRequest request);
        Task<OrganizationDto> UpdateAsync(Guid id, UpdateOrganizationRequest request);
        Task<bool> DeleteAsync(Guid id);
       // Task<string> UploadLogoToFileService(IFormFile logoFile, Guid orgId, string fileServiceBaseUrl);
    }
}
