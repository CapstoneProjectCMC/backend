using Microsoft.EntityFrameworkCore;
using OrganizationService.Core.ApiModels;
using OrganizationService.Core.Exceptions;
using OrganizationService.DataAccess.Interfaces;
using OrganizationService.DataAccess.Models;
using OrganizationService.Service.ApiModels.StudentCredential;
using OrganizationService.Service.Dtos.StudentCredential;
using OrganizationService.Service.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.Implementation
{
    public class StudentCredentialService : BaseService, IStudentCredentialService
    {
        private readonly IRepository<StudentCredential> _studentCredentialRepository;
        private readonly IRepository<Class> _classRepository;
        private readonly IRepository<Organization> _organizationRepository;

        public StudentCredentialService(
            AppSettings appSettings,
            IUnitOfWork unitOfWork,
            UserContext userContext,
            IRepository<StudentCredential> studentCredentialRepository,
            IRepository<Class> classRepository,
            IRepository<Organization> organizationRepository)
             : base(appSettings, unitOfWork, userContext)
        {
            _studentCredentialRepository = studentCredentialRepository;
            _classRepository = classRepository;
            _organizationRepository = organizationRepository;
        }

        private void ValidatePagingModel(StudentCredentialModel studentCredentialModel)
        {
            if (studentCredentialModel.PageIndex < 1)
                throw new ErrorException(Core.Enums.StatusCodeEnum.PageIndexInvalid);
            if (studentCredentialModel.PageSize < 1)
                throw new ErrorException(Core.Enums.StatusCodeEnum.PageSizeInvalid);
        }

        public async Task<IEnumerable<StudentCredentialDto>> GetViewModelsAsync(StudentCredentialModel studentCredentialModel)
        {
            // Validate PageIndex and PageSize
            ValidatePagingModel(studentCredentialModel);

            var data = await _studentCredentialRepository.AsNoTracking
                .Include(c => c.Class)
                .Include(c => c.Organization)
                .ToListAsync();
        }

        public async Task<StudentCredentialDto?> GetByIdAsync(Guid id)
        {
            var studentCredential = await _studentCredentialRepository.AsNoTracking
                .Include(c => c.Class)
                .Include(c => c.Organization)
                .FirstOrDefaultAsync(c => c.Id == id && !c.IsDeleted);

            if (studentCredential == null)
            {
                throw new ErrorException(Core.Enums.StatusCodeEnum.A02, "Student credential not found.");
            }


            return new StudentCredentialDto
            {
                Id = studentCredential.Id,
                OrganizationId = studentCredential.OrganizationId,
                ClassId = studentCredential.ClassId,
                Username = studentCredential.Username,
                IsUsed = studentCredential.IsUsed
            };
        }

        public async Task<StudentCredentialDto> CreateAsync(CreateStudentCredentialRequest request)
        {
            var credential = new StudentCredential
            {
                Id = Guid.NewGuid(),
                OrganizationId = request.OrganizationId,
                ClassId = request.ClassId,
                Username = request.Username,
                PasswordHashed = BCrypt.Net.BCrypt.HashPassword(request.Password),
                IsUsed = false,
                CreatedAt = DateTime.UtcNow,
                CreatedBy = Guid.Empty // TODO: lấy từ context
            };

            await _studentCredentialRepository.CreateAsync(credential);
            await _unitOfWork.SaveChangesAsync();

            return new StudentCredentialDto
            {
                Id = credential.Id,
                OrganizationId = credential.OrganizationId,
                ClassId = credential.ClassId,
                Username = credential.Username,
                IsUsed = credential.IsUsed
            };
        }


        public async Task<bool> UpdateAsync(Guid id, UpdateStudentCredentialRequest request)
        {
            var cred = await _context.StudentCredentials.FindAsync(id);
            if (cred == null) return false;

            if (!string.IsNullOrWhiteSpace(request.Password))
            {
                cred.PasswordHashed = BCrypt.Net.BCrypt.HashPassword(request.Password);
            }

            if (request.IsUsed.HasValue)
            {
                cred.IsUsed = request.IsUsed.Value;
            }

            cred.UpdatedAt = DateTime.UtcNow;
            cred.UpdatedBy = Guid.Empty; // TODO: lấy từ context

            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> DeleteAsync(Guid id)
        {
            var cred = await _context.StudentCredentials.FindAsync(id);
            if (cred == null) return false;

            _context.StudentCredentials.Remove(cred);
            await _context.SaveChangesAsync();
            return true;
        }
    }

}
