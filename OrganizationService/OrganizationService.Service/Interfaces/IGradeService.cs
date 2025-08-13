using OrganizationService.Service.ApiModels.Grade;
using OrganizationService.Service.Dtos.Grade;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.Interfaces
{
    public interface IGradeService
    {
        Task<IEnumerable<GradeDto>> GetViewModelsAsync(GradeModel gradeModel);
        Task<GradeDto> CreateAsync(CreateGradeRequest request);
        Task<GradeDto> GetByIdAsync(Guid id);
        Task<GradeDto> UpdateAsync(Guid id, UpdateGradeRequest request);
        Task<bool> DeleteAsync(Guid id);
    }
}
