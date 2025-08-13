using Microsoft.EntityFrameworkCore;
using OrganizationService.Core.ApiModels;
using OrganizationService.Core.Exceptions;
using OrganizationService.DataAccess.Interfaces;
using OrganizationService.DataAccess.Models;
using OrganizationService.Service.ApiModels.Grade;
using OrganizationService.Service.Dtos.Grade;
using OrganizationService.Service.Interfaces;

namespace OrganizationService.Service.Implementation
{
    public class GradeService : BaseService, IGradeService
    {
        private readonly IRepository<Grade> _gradeRepository;
        private readonly IRepository<Organization> _organizationRepository;

        public GradeService(
            AppSettings appSettings,
            IUnitOfWork unitOfWork,
            UserContext userContext,
            IRepository<Organization> organizationRepository,
            IRepository<Grade> gradeRepository
            )
            : base(appSettings, unitOfWork, userContext)
        {
            _organizationRepository = organizationRepository;
            _gradeRepository = gradeRepository;
        }

        private void ValidatePagingModel(GradeModel gradeModel)
        {
            if (gradeModel.PageIndex < 1)
                throw new ErrorException(Core.Enums.StatusCodeEnum.PageIndexInvalid);
            if (gradeModel.PageSize < 1)
                throw new ErrorException(Core.Enums.StatusCodeEnum.PageSizeInvalid);
        }
        public async Task<IEnumerable<GradeDto>> GetViewModelsAsync(GradeModel gradeModel)
        {
            // Validate PageIndex and PageSize
            ValidatePagingModel(gradeModel);

            var data = _gradeRepository.AsNoTracking
                .Where(c => !c.IsDeleted)
                .OrderByDescending(c => c.CreatedAt)
                .Select(c => new GradeDto
                {
                    Id = c.Id,
                    Name = c.Name,
                    OrganizationId = c.OrganizationId,
                    OrganizationName = _organizationRepository.AsNoTracking
                        .Where(o => o.Id == c.OrganizationId)
                        .Select(o => o.Name)
                        .FirstOrDefault() ?? string.Empty,
                });
            return await data.ToListAsync();
        }

        public async Task<GradeDto> GetByIdAsync(Guid id)
        {
            var grade = await _gradeRepository.AsNoTracking
                .FirstOrDefaultAsync(c => c.Id == id);
            if (grade == null)
            {
                throw new ErrorException(Core.Enums.StatusCodeEnum.A02, "Grade not found");
            }

            var orgName = await _organizationRepository.AsNoTracking
                .Where(o => o.Id == grade.OrganizationId)
                .Select(o => o.Name)
                .FirstOrDefaultAsync() ?? string.Empty;

            return new GradeDto
            {
                Id = grade.Id,
                Name = grade.Name,
                Description = grade.Description,
                OrganizationId = grade.OrganizationId,
                OrganizationName = orgName
            };
        }

        public async Task<GradeDto> CreateAsync(CreateGradeRequest request)
        {
            var grade = new Grade
            {
                Id = Guid.NewGuid(),
                Name = request.Name,
                Description = request.Description,
                OrganizationId = request.OrganizationId
            };

            await _gradeRepository.CreateAsync(grade);
            await _unitOfWork.SaveChangesAsync();

            var orgName = await _organizationRepository.AsNoTracking
                .Where(o => o.Id == grade.OrganizationId)
                .Select(o => o.Name)
                .FirstOrDefaultAsync() ?? string.Empty;

            return new GradeDto
            {
                Id = grade.Id,
                Name = grade.Name,
                Description = grade.Description,
                OrganizationId = grade.OrganizationId,
                OrganizationName = orgName
            };
        }

        public async Task<GradeDto> UpdateAsync(Guid id, UpdateGradeRequest request)
        {
            var grade = await _gradeRepository.FindByIdAsync(id);
            if (grade == null)
            {
                throw new ErrorException(Core.Enums.StatusCodeEnum.A02, "Grade not found");
            }

            grade.Name = request.GradeName;
            grade.Description = request.Description;
            _gradeRepository.Update(grade);

            await _unitOfWork.SaveChangesAsync();

            var orgName = await _organizationRepository.AsNoTracking
                .Where(o => o.Id == grade.OrganizationId)
                .Select(o => o.Name)
                .FirstOrDefaultAsync() ?? string.Empty;

            return new GradeDto
            {
                Id = grade.Id,
                Name = grade.Name,
                Description = grade.Description,
                OrganizationId = grade.OrganizationId,
                OrganizationName = orgName
            };
        }

        public async Task<bool> DeleteAsync(Guid id)
        {
            var grade = await _gradeRepository.FindByIdAsync(id);

            if (grade == null)
                throw new ErrorException(Core.Enums.StatusCodeEnum.A02, "Grade not found");

            grade.DeletedAt = DateTime.UtcNow;
            grade.DeletedBy = _userContext.UserId;
            grade.IsDeleted = true;

            _gradeRepository.Update(grade);
            await _unitOfWork.SaveChangesAsync();

            return true;
        }
    }
}
