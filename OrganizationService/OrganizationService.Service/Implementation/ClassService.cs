using Microsoft.EntityFrameworkCore;
using OrganizationService.Core.ApiModels;
using OrganizationService.Core.Enums;
using OrganizationService.Core.Exceptions;
using OrganizationService.DataAccess.Interfaces;
using OrganizationService.DataAccess.Models;
using OrganizationService.Service.ApiModels.Class;
using OrganizationService.Service.Dtos.Class;
using OrganizationService.Service.Interfaces;

namespace OrganizationService.Service.Implementation
{
    public class ClassService : BaseService, IClassService
    {
        private readonly IRepository<Class> _classRepository;
        private readonly IRepository<Grade> _gradeRepository;

        public ClassService(
            AppSettings appSettings,
            IUnitOfWork unitOfWork,
            UserContext userContext,
            IRepository<Class> classRepository,
            IRepository<Grade> gradeRepository)
            : base(appSettings, unitOfWork, userContext)
        {
            _classRepository = classRepository;
            _gradeRepository = gradeRepository;
        }

        private void ValidatePagingModel(ClassModel classModel)
        {
            if (classModel.PageIndex < 1)
                throw new ErrorException(Core.Enums.StatusCodeEnum.PageIndexInvalid);
            if (classModel.PageSize < 1)
                throw new ErrorException(Core.Enums.StatusCodeEnum.PageSizeInvalid);
        }

        public async Task<IEnumerable<ClassDto>> GetViewModelsAsync(ClassModel classModel)
        {
            // Validate PageIndex and PageSize
            ValidatePagingModel(classModel);

            var query = _classRepository.AsNoTracking.Include(c => c.Grade).Where(c => !c.IsDeleted);

            return await query.Select(c => new ClassDto
            {
                Id = c.Id,
                Name = c.Name,
                Description = c.Description,
                GradeId = c.GradeId,
                GradeName = c.Grade.Name
            }).ToListAsync();
        }

        public async Task<ClassDto> GetByIdAsync(Guid id)
        {
            var data = await _classRepository.AsNoTracking
                .Include(c => c.Grade)
                .FirstOrDefaultAsync(c => c.Id == id && !c.IsDeleted);

            if (data == null)
            {
                throw new ErrorException(StatusCodeEnum.A02, "Class not found");
            }

            return new ClassDto
            {
                Id = data.Id,
                Name = data.Name,
                Description = data.Description,
                GradeId = data.GradeId,
                GradeName = data.Grade.Name
            };
        }

        public async Task<ClassDto> CreateAsync(CreateClassRequest request)
        {
            var grade = await _gradeRepository.FindByIdAsync(request.GradeId)
                        ?? throw new ErrorException(StatusCodeEnum.A02, "Grade not found");

            var entity = new Class
            {
                Id = Guid.NewGuid(),
                Name = request.ClassName,
                Description = request.Description,
                GradeId = request.GradeId,
                CreatedAt = DateTime.UtcNow
            };

            await _classRepository.CreateAsync(entity);
            await _unitOfWork.SaveChangesAsync();

            return new ClassDto
            {
                Id = entity.Id,
                Name = entity.Name,
                Description = entity.Description,
                GradeId = entity.GradeId,
                GradeName = grade.Name
            };
        }

        public async Task<bool> UpdateAsync(Guid id, UpdateClassRequest request)
        {
            var entity = await _classRepository.FindByIdAsync(id);
            if (entity == null)
            {
                throw new ErrorException(StatusCodeEnum.A02, "Class not found");
            }

            if (!string.IsNullOrWhiteSpace(request.Name)) entity.Name = request.Name;
            if (request.GradeId.HasValue) entity.GradeId = request.GradeId.Value;

            entity.UpdatedAt = DateTime.UtcNow;

            await _unitOfWork.SaveChangesAsync();
            return true;
        }

        public async Task<bool> DeleteAsync(Guid id)
        {
            var entity = await _classRepository.FindByIdAsync(id);
            if (entity == null)
            {
                throw new ErrorException(StatusCodeEnum.A02, "Class not found");
            }
            
            entity.IsDeleted = true;

            _classRepository.Delete(entity);
            await _unitOfWork.SaveChangesAsync();
            return true;
        }
    }
}
