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
using OrganizationService.Core.Constants;

namespace OrganizationService.Service.Implementation
{
    public class OrganizationMemberService : BaseService, IOrganizationMemberService
    {
        private readonly IRepository<OrganizationMember> _organizationMemberRepository;
        private readonly IRepository<Organization> _organizationRepository;
        private readonly IOrgMemberEventPublisher _orgMemberEventPublisher;
        
        public OrganizationMemberService(
            AppSettings appSettings,
            IUnitOfWork unitOfWork,
            UserContext userContext,
            IRepository<OrganizationMember> organizationMemberRepository,
            IRepository<Organization> organizationRepository,
            IOrgMemberEventPublisher orgMemberEventPublisher
        ) : base(appSettings, unitOfWork, userContext)
        {
            _organizationMemberRepository = organizationMemberRepository;
            _organizationRepository = organizationRepository;
            _orgMemberEventPublisher = orgMemberEventPublisher;
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
            
            await _orgMemberEventPublisher.PublishAsync(new {
                type = "ADDED",
                userId = entity.UserId,
                scopeType = entity.ScopeType.ToString().ToUpper(), // ORGANIZATION/GRADE/CLASS
                scopeId = entity.ScopeId,
                role = entity.Role.ToString(), // SuperAdmin/Admin/Teacher/Student
                isActive = entity.IsActive,
                at = DateTime.UtcNow
            });

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
            
            await _orgMemberEventPublisher.PublishAsync(new {
                type = "UPDATED",
                userId = entity.UserId,
                scopeType = entity.ScopeType.ToString().ToUpper(), // ORGANIZATION/GRADE/CLASS
                scopeId = entity.ScopeId,
                role = entity.Role.ToString(), // SuperAdmin/Admin/Teacher/Student
                isActive = entity.IsActive,
                at = DateTime.UtcNow
            });

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
            
            await _orgMemberEventPublisher.PublishAsync(new {
                type = "DELETED",
                userId = entity.UserId,
                scopeType = entity.ScopeType.ToString().ToUpper(),
                scopeId = entity.ScopeId,
                role = entity.Role.ToString(),
                isActive = false,
                at = DateTime.UtcNow
            });
            return true;
        }

        public async Task<PrimaryOrgDto?> GetPrimaryOrgForUser(Guid userId)
        {
            // Lấy membership active gần nhất ở phạm vi Organization
            var member = await _organizationMemberRepository.AsNoTracking
                .Where(orgMember => !orgMember.IsDeleted
                        && orgMember.UserId == userId
                        && orgMember.ScopeType == ScopeType.Organization
                        && orgMember.IsActive)
                .OrderByDescending(orgMember => orgMember.CreatedAt)
                .FirstOrDefaultAsync();
            
            if (member == null) return null;

            return new PrimaryOrgDto
            {
                OrganizationId = member.ScopeId,
                Role = member.Role,
            };
        }

        public async Task<OrganizationMemberDto> JoinOrganizationForCurrentUser(Guid organizationId)
        {
            // Current user từ _userContext
            if (_userContext.UserId == Guid.Empty)
                throw new ErrorException(Core.Enums.StatusCodeEnum.BadRequest, "Unauthenticated");

            // Kiểm tra đã là member chưa
            var existed = await _organizationMemberRepository.AsNoTracking
                .AnyAsync(orgMember => !orgMember.IsDeleted
                               && orgMember.UserId == _userContext.UserId
                               && orgMember.ScopeType == ScopeType.Organization
                               && orgMember.ScopeId == organizationId);

            if (existed)
                throw new ErrorException(Core.Enums.StatusCodeEnum.InvalidOption, "Already a member");

            var entity = new OrganizationMember
            {
                Id = Guid.NewGuid(),
                UserId = _userContext.UserId,
                ScopeType = ScopeType.Organization,
                ScopeId = organizationId,
                Role = MemberRole.Student,   // mặc định user join là Student
                IsActive = true
            };

            await _organizationMemberRepository.CreateAsync(entity);
            await _unitOfWork.SaveChangesAsync();

            var orgName = (await _organizationRepository.FindByIdAsync(organizationId))?.Name ?? string.Empty;

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

        public async  Task<OrganizationMemberDto> GrantAdmin(Guid organizationId, Guid userId)
        {
            // Có thể bổ sung check quyền hiện tại nếu cần (SuperAdmin/Admin hệ thống)
            var member = await _organizationMemberRepository.AsNoTracking
                .FirstOrDefaultAsync(orgMember => !orgMember.IsDeleted
                                          && orgMember.UserId == userId
                                          && orgMember.ScopeType == ScopeType.Organization
                                          && orgMember.ScopeId == organizationId);

            if (member == null)
            {
                member = new OrganizationMember
                {
                    Id = Guid.NewGuid(),
                    UserId = userId,
                    ScopeType = ScopeType.Organization,
                    ScopeId = organizationId,
                    IsActive = true
                };
                await _organizationMemberRepository.CreateAsync(member);
            }

            member.Role = MemberRole.Admin;
            _organizationMemberRepository.Update(member);
            await _unitOfWork.SaveChangesAsync();

            var orgName = (await _organizationRepository.FindByIdAsync(organizationId))?.Name ?? string.Empty;

            return new OrganizationMemberDto
            {
                Id = member.Id,
                UserId = member.UserId,
                ScopeType = member.ScopeType,
                ScopeId = member.ScopeId,
                Role = member.Role,
                IsActive = member.IsActive,
                ScopeName = orgName
            };
        }
        
        public async Task<OrganizationMemberDto> CreateOrUpdateAsync(CreateOrganizationMemberRequest request)
        {
            // tìm membership trùng (UserId, ScopeType, ScopeId)
            var existed = await _organizationMemberRepository.AsNoTracking
                .FirstOrDefaultAsync(x => !x.IsDeleted
                    && x.UserId == request.UserId
                    && x.ScopeType == request.ScopeType
                    && x.ScopeId == request.ScopeId);

            var now = DateTime.UtcNow;
            if (existed == null)
            {
                var entity = new OrganizationMember
                {
                    Id = Guid.NewGuid(),
                    UserId = request.UserId,
                    ScopeType = request.ScopeType,
                    ScopeId = request.ScopeId,
                    Role = request.Role,
                    IsActive = request.IsActive,
                    CreatedAt = now
                };
                await _organizationMemberRepository.CreateAsync(entity);
                await _unitOfWork.SaveChangesAsync();

                var orgName = (await _organizationRepository.FindByIdAsync(entity.ScopeId))?.Name ?? string.Empty;
                await _orgMemberEventPublisher.PublishAsync(new {
                    type = "ADDED",
                    userId = entity.UserId,
                    scopeType = entity.ScopeType.ToString().ToUpper(),
                    scopeId = entity.ScopeId,
                    role = entity.Role.ToString(),
                    isActive = entity.IsActive,
                    at = now
                });
                return new OrganizationMemberDto {
                    Id = entity.Id, UserId = entity.UserId, ScopeType = entity.ScopeType, ScopeId = entity.ScopeId,
                    Role = entity.Role, IsActive = entity.IsActive, ScopeName = orgName
                };
            }
            else
            {
                // cập nhật idempotent
                var entity = await _organizationMemberRepository.FindByIdAsync(existed.Id);
                entity.Role = request.Role;
                entity.IsActive = request.IsActive;
                entity.UpdatedAt = now;
                _organizationMemberRepository.Update(entity);
                await _unitOfWork.SaveChangesAsync();

                var orgName = (await _organizationRepository.FindByIdAsync(entity.ScopeId))?.Name ?? string.Empty;
                await _orgMemberEventPublisher.PublishAsync(new {
                    type = "UPDATED",
                    userId = entity.UserId,
                    scopeType = entity.ScopeType.ToString().ToUpper(),
                    scopeId = entity.ScopeId,
                    role = entity.Role.ToString(),
                    isActive = entity.IsActive,
                    at = now
                });
                return new OrganizationMemberDto {
                    Id = entity.Id, UserId = entity.UserId, ScopeType = entity.ScopeType, ScopeId = entity.ScopeId,
                    Role = entity.Role, IsActive = entity.IsActive, ScopeName = orgName
                };
            }
        }

        public async Task<IEnumerable<OrganizationMemberDto>> BulkCreateOrUpdateAsync(IEnumerable<CreateOrganizationMemberRequest> requests)
        {
            var result = new List<OrganizationMemberDto>();
            foreach (var r in requests)
            {
                var one = await CreateOrUpdateAsync(r);
                result.Add(one);
            }
            return result;
        }

    }
}
