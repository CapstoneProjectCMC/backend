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

        public OrganizationService(
            AppSettings appSettings,
            IUnitOfWork unitOfWork,
            UserContext userContext,
            IRepository<Organization> organizationRepository,
            FileServiceClient fileServiceClient
        ) : base(appSettings, unitOfWork, userContext)
        {
            _organizationRepository = organizationRepository;
            _fileServiceClient = fileServiceClient;
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
                 //   Logo = o.Logo,
                    LogoUrl = o.LogoUrl,
                    Status = o.Status
                });
               

            return await orgs.ToListAsync();
        }

        public async Task<OrganizationDto> GetByIdAsync(Guid id)
        {
            var org = await _organizationRepository.AsNoTracking
                .FirstOrDefaultAsync(o => o.Id == id && !o.IsDeleted);

            if (org == null)
                throw new ErrorException(Core.Enums.StatusCodeEnum.A02, "Organization not found");

            return new OrganizationDto
            {
                Id = org.Id,
                Name = org.Name,
                Description = org.Description,
                Address = org.Address,
                Email = org.Email,
                Phone = org.Phone,
              //  Logo = org.Logo,
                LogoUrl = org.LogoUrl,
                Status = org.Status
            };
        }

        public async Task<OrganizationDto> CreateAsync(CreateOrganizationRequest request)
        {
            var existingOrg = await _organizationRepository.FindAsync(o => o.Name.ToLower() == request.Name.ToLower());
            if (existingOrg != null)
                throw new ErrorException(Core.Enums.StatusCodeEnum.A02, "Organization with this name already exists");


            var entity = new Organization
            {
                Id = Guid.NewGuid(),
                Name = request.Name,
                Description = request.Description,
                Address = request.Address,
                Email = request.Email,
                Phone = request.Phone,
              //  Logo = request?.Logo,
               // LogoUrl = request?.LogoUrl,
                Status = request.Status,
                CreatedAt = DateTime.UtcNow,
                CreatedBy = _userContext.UserId
            };

            await _organizationRepository.CreateAsync(entity);
            await _unitOfWork.SaveChangesAsync();

            // Nếu có logo file, upload lên File service
            if (request.LogoUrl != null && request.LogoUrl.Length > 0)
            {
                try
                {
                    var logoUrl = await _fileServiceClient.UploadLogoAsync(request.LogoUrl, entity.Id);
                    entity.LogoUrl = logoUrl;

                    _organizationRepository.Update(entity);
                    await _unitOfWork.SaveChangesAsync();
                }
                catch (ErrorException ex)
                {
                    throw new ErrorException(ex.StatusCode, $"Failed to upload logo: {ex.Message}");
                }
                catch (Exception ex)
                {
                    throw new ErrorException(Core.Enums.StatusCodeEnum.A01, $"Failed to upload logo: {ex.Message}");
                }
            }

            return new OrganizationDto
            {
                Id = entity.Id,
                Name = entity.Name,
                Description = entity.Description,
                Address = entity.Address,
                Email = entity.Email,
                Phone = entity.Phone,
                //Logo = entity.Logo,
                LogoUrl = entity.LogoUrl,
                Status = entity.Status
            };
        }

        public async Task<OrganizationDto> UpdateAsync(Guid id, UpdateOrganizationRequest request)
        {
            var entity = await _organizationRepository.FindByIdAsync(id);
            if (entity == null)
                throw new ErrorException(Core.Enums.StatusCodeEnum.A02, "Organization not found");

            entity.Name = request.Name;
            entity.Description = request.Description;
            entity.Address = request.Address;
            entity.Email = request.Email;
            entity.Phone = request.Phone;
            entity.Logo = request.Logo;
            entity.LogoUrl = request.LogoUrl;
            entity.Status = request.Status;
            entity.UpdatedAt = DateTime.UtcNow;
            entity.UpdatedBy = _userContext.UserId;

            _organizationRepository.Update(entity);
            await _unitOfWork.SaveChangesAsync();

            return new OrganizationDto
            {
                Id = entity.Id,
                Name = entity.Name,
                Description = entity.Description,
                Address = entity.Address,
                Email = entity.Email,
                Phone = entity.Phone,
               // Logo = entity.Logo,
                LogoUrl = entity.LogoUrl,
                Status = entity.Status
            };
        }

        public async Task<bool> DeleteAsync(Guid id)
        {
            var entity = await _organizationRepository.FindByIdAsync(id);
            if (entity == null)
                throw new ErrorException(Core.Enums.StatusCodeEnum.A02, "Organization not found");

            entity.IsDeleted = true;
            entity.DeletedAt = DateTime.UtcNow;
            entity.DeletedBy = _userContext.UserId;

            _organizationRepository.Update(entity);
            await _unitOfWork.SaveChangesAsync();
            return true;
        }
    }
}
