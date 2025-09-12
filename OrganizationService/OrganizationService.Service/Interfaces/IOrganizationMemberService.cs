using OrganizationService.Core.Constants;
using OrganizationService.Service.ApiModels.OrganizationMember;
using OrganizationService.Service.Dtos.OrganizationMember;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.Interfaces
{
    public interface IOrganizationMemberService
    {
        Task<IEnumerable<OrganizationMemberDto>> GetViewModelsAsync(OrganizationMemberModel orgMemberModel);
        Task<OrganizationMemberDto> GetByIdAsync(Guid id);
        Task<OrganizationMemberDto> CreateAsync(CreateOrganizationMemberRequest request);
        Task<OrganizationMemberDto> UpdateAsync(Guid id, UpdateOrganizationMemberRequest request);
        Task<bool> DeleteAsync(Guid id);
        
        Task<PrimaryOrgDto?> GetPrimaryOrgForUser(Guid userId);
        Task<OrganizationMemberDto> JoinOrganizationForCurrentUser(Guid organizationId);
        Task<OrganizationMemberDto> GrantAdmin(Guid organizationId, Guid userId);
        
        Task<OrganizationMemberDto> CreateOrUpdateAsync(CreateOrganizationMemberRequest request);
        Task<IEnumerable<OrganizationMemberDto>> BulkCreateOrUpdateAsync(
            IEnumerable<CreateOrganizationMemberRequest> requests);
    }
}
