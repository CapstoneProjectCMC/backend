using Microsoft.EntityFrameworkCore;
using OrganizationService.Core.ApiModels;
using OrganizationService.Core.Exceptions;
using OrganizationService.DataAccess.Interfaces;
using OrganizationService.DataAccess.Models;
using OrganizationService.Service.ApiModels.OrganizationMember;
using OrganizationService.Service.Dtos.OrganizationMember;
using OrganizationService.Service.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.Implementation
{
    public class OrganizationMemberService : BaseService, IOrganizationMemberService
    {
        private readonly IRepository<OrganizationMember> _organizationMemberRepository;
        private readonly IRepository<Organization> _organizationRepository;


        public OrganizationMemberService(
            AppSettings appSettings,
            IUnitOfWork unitOfWork,
            UserContext userContext,
            IRepository<OrganizationMember> organizationMemberRepository,
            IRepository<Organization> organizationRepository
        ) : base(appSettings, unitOfWork, userContext)
        {
            _organizationMemberRepository = organizationMemberRepository;
            _organizationRepository = organizationRepository;
        }

        private void ValidatePagingModel(OrganizationMemberModel orgMemberModel)
        {
            if (orgMemberModel.PageIndex < 1)
                throw new ErrorException(Core.Enums.StatusCodeEnum.PageIndexInvalid);
            if (orgMemberModel.PageSize < 1)
                throw new ErrorException(Core.Enums.StatusCodeEnum.PageSizeInvalid);
        }

        public async Task<IEnumerable<OrganizationMemberDto>> GetViewModelsAsync(OrganizationMemberModel orgMemberModel)
        {

            // Validate PageIndex and PageSize
            ValidatePagingModel(orgMemberModel);

            var query = _organizationMemberRepository.AsNoTracking
                .Where(x => !x.IsDeleted)
                .OrderByDescending(x => x.CreatedAt);

            var list = await query.ToListAsync();

            var result = new List<OrganizationMemberDto>();
            foreach (var m in list)
            {
                var orgName = (await _organizationRepository.FindByIdAsync(m.ScopeId))?.Name ?? string.Empty;

                result.Add(new OrganizationMemberDto
                {
                    Id = m.Id,
                    UserId = m.UserId,
                    ScopeType = m.ScopeType,
                    ScopeId = m.ScopeId,
                    Role = m.Role,
                    IsActive = m.IsActive,
                    ScopeName = orgName
                });
            }

            return result;
        }

        public async Task<OrganizationMemberDto> GetByIdAsync(Guid id)
        {
            var m = await _organizationMemberRepository.AsNoTracking
                .FirstOrDefaultAsync(x => x.Id == id && !x.IsDeleted);

            if (m == null)
                throw new ErrorException(Core.Enums.StatusCodeEnum.A02, "Organization member not found");

            var orgName = (await _organizationRepository.FindByIdAsync(m.ScopeId))?.Name ?? string.Empty;

            return new OrganizationMemberDto
            {
                Id = m.Id,
                UserId = m.UserId,
                ScopeType = m.ScopeType,
                ScopeId = m.ScopeId,
                Role = m.Role,
                IsActive = m.IsActive,
                ScopeName = orgName
            };
        }

        public async Task<OrganizationMemberDto> CreateAsync(CreateOrganizationMemberRequest request)
        {
            var entity = new OrganizationMember
            {
                Id = Guid.NewGuid(),
                UserId = request.UserId,
                ScopeType = request.ScopeType,
                ScopeId = request.ScopeId,
                Role = request.Role,
                IsActive = request.IsActive
            };

            await _organizationMemberRepository.CreateAsync(entity);
            await _unitOfWork.SaveChangesAsync();

            var orgName = (await _organizationRepository.FindByIdAsync(entity.ScopeId))?.Name ?? string.Empty;

            return new OrganizationMemberDto
            {
                Id = entity.Id,
                UserId = entity.UserId,
                ScopeType = entity.ScopeType,
                ScopeId = entity.ScopeId,
                Role = entity.Role,
                IsActive = entity.IsActive,
                ScopeName = orgName
            };
        }

        public async Task<OrganizationMemberDto> UpdateAsync(Guid id, UpdateOrganizationMemberRequest request)
        {
            var entity = await _organizationMemberRepository.FindByIdAsync(id);

            if (entity == null)
                throw new ErrorException(Core.Enums.StatusCodeEnum.A02, "Organization member not found");

            entity.Role = request.Role;
            entity.IsActive = request.IsActive;

            _organizationMemberRepository.Update(entity);
            await _unitOfWork.SaveChangesAsync();

            var orgName = (await _organizationRepository.FindByIdAsync(entity.ScopeId))?.Name ?? string.Empty;

            return new OrganizationMemberDto
            {
                Id = entity.Id,
                UserId = entity.UserId,
                ScopeType = entity.ScopeType,
                ScopeId = entity.ScopeId,
                Role = entity.Role,
                IsActive = entity.IsActive,
                ScopeName = orgName
            };
        }

        public async Task<bool> DeleteAsync(Guid id)
        {
            var entity = await _organizationMemberRepository.FindByIdAsync(id);

            if (entity == null)
                throw new ErrorException(Core.Enums.StatusCodeEnum.A02, "Organization member not found");

            _organizationMemberRepository.Delete(entity);
            await _unitOfWork.SaveChangesAsync();
            return true;
        }
    }
}
