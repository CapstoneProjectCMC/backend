using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using OrganizationService.Core.ApiModels;
using OrganizationService.Service.ApiModels.Class;
using OrganizationService.Service.ApiModels.Grade;
using OrganizationService.Service.Dtos.Class;
using OrganizationService.Service.Dtos.Grade;
using OrganizationService.Service.Interfaces;

namespace OrganizationService.Api.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class GradeController : BaseApiController
    {
        public IGradeService _gradeService { get; set; }
        private readonly IConfiguration _configuration;

        public GradeController(IServiceProvider serviceProvider, IGradeService gradeService, IConfiguration configuration) : base(serviceProvider)
        {
            _configuration = configuration;
            _gradeService = gradeService;
        }

        [HttpPost("public")]
        public async Task<IActionResult> GetViewModelsAsync([FromBody] GradeModel gradeModel)
        {
            var result = await _gradeService.GetViewModelsAsync(gradeModel);
            var listResult = new PaginatedList<GradeDto>(result.ToList(), result.Count(), gradeModel.PageIndex, gradeModel.PageSize);
            return Success(listResult);
        }

        //get project by id
        [HttpGet("{id}")]
        public async Task<IActionResult> GetGradeById(Guid id)
        {
            var result = await _gradeService.GetByIdAsync(id);
            return Success(result);
        }

        //add project
        [HttpPost("add")]
        public async Task<IActionResult> AddGrade([FromBody] CreateGradeRequest addGradeModel)
        {
            await _gradeService.CreateAsync(addGradeModel);
            return Success();
        }

        //edit project
        [HttpPut("edit/{gradeId}")]
        public async Task<IActionResult> EditDetailGrade(Guid gradeId, [FromBody] UpdateGradeRequest editGradeModel)
        {
            var result = await _gradeService.UpdateAsync(gradeId, editGradeModel);
            return Success(result);
        }

        //delete project
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteGrade(Guid id)
        {
           var result = await _gradeService.DeleteAsync(id);
            return Success(result);
        }
    }
}
