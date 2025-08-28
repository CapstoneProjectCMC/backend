using Microsoft.AspNetCore.Http;
using Microsoft.EntityFrameworkCore;
using OrganizationService.Core.ApiModels;
using OrganizationService.Core.Exceptions;
using OrganizationService.DataAccess.Interfaces;
using OrganizationService.DataAccess.Models;
using OrganizationService.Service.ApiModels.Grade;
using OrganizationService.Service.ApiModels.Organization;
using OrganizationService.Service.Dtos.Organization;
using OrganizationService.Service.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace OrganizationService.Service.Implementation
{
    public class OrganizationService : BaseService, IOrganizationService
    {
        private readonly IRepository<Organization> _organizationRepository;
        private readonly FileServiceClient _fileServiceClient;
        private readonly IOrgEventPublisher _orgEventPublisher;

        public OrganizationService(
            AppSettings appSettings,
            IUnitOfWork unitOfWork,
            UserContext userContext,
            IRepository<Organization> organizationRepository,
            FileServiceClient fileServiceClient,
            IOrgEventPublisher orgEventPublisher
        ) : base(appSettings, unitOfWork, userContext)
        {
            _organizationRepository = organizationRepository;
            _fileServiceClient = fileServiceClient;
            _orgEventPublisher = orgEventPublisher;
        }

        private void ValidatePagingModel(OrganizationModel orgModel)
        {
            if (orgModel.PageIndex < 1)
                throw new ErrorException(Core.Enums.StatusCodeEnum.PageIndexInvalid);
            if (orgModel.PageSize < 1)
                throw new ErrorException(Core.Enums.StatusCodeEnum.PageSizeInvalid);
        }

        public async Task<IEnumerable<OrganizationDto>> GetViewModelsAsync(OrganizationModel orgModel)
        {

            // Validate PageIndex and PageSize
            ValidatePagingModel(orgModel);

            var orgs = _organizationRepository.AsNoTracking
                .Where(o => !o.IsDeleted)
                .OrderByDescending(o => o.CreatedAt)
                .Select(o => new OrganizationDto
                {
                    Id = o.Id,
                    Name = o.Name,
                    Description = o.Description,
                    Address = o.Address,
                    Email = o.Email,
                    Phone = o.Phone,
                    LogoId = o.Logo,
                    LogoUrl = o.LogoUrl,
                    Status = o.Status,
                    CreatedAt = o.CreatedAt,
                    CreatedBy = _userContext.UserId,
                    OrganizationRole = _userContext.OrganizationRole,
                });

            return await orgs.ToListAsync();
        }

        public async Task<OrganizationDto> GetByIdAsync(Guid id)
        {
            var org = await _organizationRepository.AsNoTracking
                .FirstOrDefaultAsync(o => o.Id == id && !o.IsDeleted);

            if (org == null)
                throw new ErrorException(Core.Enums.StatusCodeEnum.D01);

            return new OrganizationDto
            {
                Id = org.Id,
                Name = org.Name,
                Description = org.Description,
                Address = org.Address,
                Email = org.Email,
                Phone = org.Phone,
                LogoId = org.Logo,
                LogoUrl = org.LogoUrl,
                Status = org.Status,
                CreatedAt = org.CreatedAt,
                CreatedBy = _userContext.UserId,
                OrganizationRole = _userContext.OrganizationRole,
            };
        }

        public async Task<OrganizationDto> CreateAsync(CreateOrganizationRequest request)
        {
            var requestName = request.Name.ToLower().Trim();
            var existingOrg = await _organizationRepository.FindAsync(o => o.Name.ToLower().Trim() == request.Name.ToLower().Trim());
            Console.WriteLine($"Request Name: {requestName}, Existing Name: {existingOrg?.Name}");
            if (existingOrg != null)
                throw new ErrorException(Core.Enums.StatusCodeEnum.D02);

            var entity = new Organization
            {
                Id = Guid.NewGuid(),
                Name = request.Name,
                Description = request.Description,
                Address = request.Address,
                Email = request.Email,
                Phone = request.Phone,
                Status = request.Status,
                CreatedAt = DateTime.UtcNow,
                CreatedBy = _userContext.UserId,
            };

            // Nếu có logo file, upload lên File service
            if (request.LogoFile != null && request.LogoFile.Length > 0)
            {
                try
                {
                    var logoResult = await _fileServiceClient.UploadLogoAsync(request.LogoFile, entity.Id);
                    if (logoResult == null || string.IsNullOrEmpty(logoResult.Url) ||logoResult.Url.ToString() == null)
                    {
                        throw new ErrorException(Core.Enums.StatusCodeEnum.D03, "Failed to upload logo file.");
                    }

                    entity.Logo = logoResult.FileId;
                    entity.LogoUrl = logoResult.Url;

                }
                catch (ErrorException)
                {
                    throw new ErrorException(Core.Enums.StatusCodeEnum.D03);
                }
                catch (Exception ex)
                {
                    throw new ErrorException(Core.Enums.StatusCodeEnum.D03, ex.Message);
                }
            }
            else
            {
                entity.Logo = null;
                entity.LogoUrl = null;
            }

            await _organizationRepository.CreateAsync(entity);
            await _unitOfWork.SaveChangesAsync();

            //publish event after organization is created
             _orgEventPublisher.PublishAsync(new {
                type = "CREATED",
                id = entity.Id,
                scopeType = "ORGANIZATION",
                payload = new {
                    name = entity.Name,
                    description = entity.Description,
                    logoUrl = entity.LogoUrl,
                    email = entity.Email,
                    phone = entity.Phone,
                    address = entity.Address,
                    status = entity.Status.ToString(),
                    createdAt = entity.CreatedAt,
                }
            });
            
            return MapToDto(entity);
        }

        public async Task<OrganizationDto> UpdateAsync(Guid id, UpdateOrganizationRequest request)
        {
            var entity = await _organizationRepository.FindByIdAsync(id);
            if (entity == null)
                throw new ErrorException(Core.Enums.StatusCodeEnum.D01);

            entity.Name = request.Name ?? entity.Name;
            entity.Description = request.Description ?? entity.Description;
            entity.Address = request.Address ?? entity.Address;
            entity.Email = request.Email ?? entity.Email;
            entity.Phone = request.Phone ?? entity.Phone;
            entity.Status = request.Status;

            // Nếu update có file logo mới
            if (request.LogoFile != null && request.LogoFile.Length > 0)
            {
                var logoResult = await _fileServiceClient.UpdateLogoAsync(entity.Id, entity.Logo.Value, request.LogoFile);
                entity.LogoUrl = logoResult.Url;
            }

            entity.UpdatedAt = DateTime.UtcNow;
            entity.UpdatedBy = _userContext.UserId;

            _organizationRepository.Update(entity);
            await _unitOfWork.SaveChangesAsync();

            await _orgEventPublisher.PublishAsync(new
            {
                type = "UPDATED",
                id = entity.Id,
                scopeType = "ORGANIZATION",
                payload = new
                {
                    name = entity.Name,
                    description = entity.Description,
                    logoUrl = entity.LogoUrl,
                    email = entity.Email,
                    phone = entity.Phone,
                    address = entity.Address,
                    status = entity.Status.ToString(),
                    updatedAt = entity.UpdatedAt,
                    updatedBy = _userContext.UserId
                }
            });

            return MapToDto(entity);
        }

        private OrganizationDto MapToDto(Organization entity)
        {
            return new OrganizationDto
            {
                Id = entity.Id,
                Name = entity.Name,
                Description = entity.Description,
                Address = entity.Address,
                Email = entity.Email,
                Phone = entity.Phone,
                LogoId = entity.Logo,
                LogoUrl = entity.LogoUrl,
                Status = entity.Status,
                OrganizationRole = _userContext.OrganizationRole,
                CreatedBy = _userContext.UserId
            };
        }

        public async Task<bool> DeleteAsync(Guid id)
        {
            var entity = await _organizationRepository.FindByIdAsync(id);
            if (entity == null)
                throw new ErrorException(Core.Enums.StatusCodeEnum.D01);

            entity.IsDeleted = true;
            entity.DeletedAt = DateTime.UtcNow;
            entity.DeletedBy = _userContext.UserId;

            _organizationRepository.Update(entity);
            await _unitOfWork.SaveChangesAsync();
            
            await _orgEventPublisher.PublishAsync(new {
                type = "DELETED",
                id = entity.Id,
                scopeType = "ORGANIZATION",
                payload = (object)null
            });
            return true;
        }
    }
}
