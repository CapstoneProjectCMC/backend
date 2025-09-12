using Microsoft.EntityFrameworkCore;
using OrganizationService.Core.ApiModels;
using OrganizationService.Core.Enums;
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
using static Microsoft.EntityFrameworkCore.DbLoggerCategory;

namespace OrganizationService.Service.Implementation
{
    public class StudentCredentialService : BaseService, IStudentCredentialService
    {
        private readonly IRepository<StudentCredential> _studentCredentialRepository;

        public StudentCredentialService(
            AppSettings appSettings,
            IUnitOfWork unitOfWork,
            UserContext userContext,
            IRepository<StudentCredential> studentCredentialRepository)
             : base(appSettings, unitOfWork, userContext)
        {
            _studentCredentialRepository = studentCredentialRepository;
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

            var data = _studentCredentialRepository.AsNoTracking
             .Include(c => c.Class)
             .Include(c => c.Organization)
             .Where(c => !c.IsDeleted)
             .OrderByDescending(c => c.CreatedAt)
             .Select(c => new StudentCredentialDto
             {
                 Id = c.Id,
                 OrganizationId = c.OrganizationId,
                 ClassId = c.ClassId,
                 Username = c.Username,
                 IsUsed = c.IsUsed,
                 ClassName = c.Class.Name,
                 OrganizationName = c.Organization.Name
             });

            var list = await data.ToListAsync(); 

            return list;
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
                OrganizationName = studentCredential.Organization.Name,
                ClassId = studentCredential.ClassId,
                ClassName = studentCredential.Class.Name,
                Username = studentCredential.Username,
                IsUsed = studentCredential.IsUsed
            };
        }

        public async Task<StudentCredentialDto> CreateAsync(CreateStudentCredentialRequest request)
        {

            var exists = await _studentCredentialRepository.FindAsync(x => x.OrganizationId == request.OrganizationId && x.Username == request.Username && !x.IsDeleted);
            if (exists != null)
            {
                throw new ErrorException(StatusCodeEnum.A01, "Username already exists.");
            }

            var credential = new StudentCredential
            {
                Id = Guid.NewGuid(),
                OrganizationId = request.OrganizationId,
                ClassId = request.ClassId,
                Username = request.Username,
                PasswordHashed = BCrypt.Net.BCrypt.HashPassword(request.Password),
                IsUsed = false,
                CreatedAt = DateTime.UtcNow
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
            var cred = await _studentCredentialRepository.FindByIdAsync(id);
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

            await _unitOfWork.SaveChangesAsync();
            return true;
        }

        public async Task<bool> DeleteAsync(Guid id)
        {
            var data = await _studentCredentialRepository.FindByIdAsync(id);
            if (data == null)
            {
                throw new ErrorException(Core.Enums.StatusCodeEnum.A02, "Student credential not found.");
            }

            data.IsDeleted = true;

            _studentCredentialRepository.Delete(data);
            await _unitOfWork.SaveChangesAsync();
            return true;
        }
    }
}
