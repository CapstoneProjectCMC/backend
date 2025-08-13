using OrganizationService.Service.ApiModels.Class;
using OrganizationService.Service.Dtos.Class;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.Interfaces
{
    public interface IClassService
    {
        Task<IEnumerable<ClassDto>> GetViewModelsAsync(ClassModel classModel);
        Task<ClassDto> GetByIdAsync(Guid id);
        Task<ClassDto> CreateAsync(CreateClassRequest request);
        Task<bool> UpdateAsync(Guid id, UpdateClassRequest request);
        Task<bool> DeleteAsync(Guid id);
    }
}
