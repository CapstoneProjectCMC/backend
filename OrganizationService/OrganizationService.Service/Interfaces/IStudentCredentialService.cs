using OrganizationService.Service.ApiModels.StudentCredential;
using OrganizationService.Service.Dtos.StudentCredential;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.Interfaces
{
    public interface IStudentCredentialService
    {
        Task<StudentCredentialDto> CreateAsync(CreateStudentCredentialRequest request);
        Task<StudentCredentialDto?> GetByIdAsync(Guid id);
        Task<IEnumerable<StudentCredentialDto>> GetByClassIdAsync(Guid classId);
        Task<bool> UpdateAsync(Guid id, UpdateStudentCredentialRequest request);
        Task<bool> DeleteAsync(Guid id);
    }

}
