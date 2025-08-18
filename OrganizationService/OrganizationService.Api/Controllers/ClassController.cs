using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using OrganizationService.Core.ApiModels;
using OrganizationService.Service.ApiModels.Class;
using OrganizationService.Service.Dtos.Class;
using OrganizationService.Service.Implementation;
using OrganizationService.Service.Interfaces;

namespace OrganizationService.Api.Controllers
{
    [Authorize]
    [Authorize(Policy = "AdminOnly")]
    [Route("api/[controller]")]
    [ApiController]
    public class ClassController : BaseApiController
    {
        public IClassService _classService { get; set; }
        private readonly IConfiguration _configuration;

        public ClassController(IServiceProvider serviceProvider, IClassService classService, IConfiguration configuration) : base(serviceProvider)
        {
            _configuration = configuration;
            _classService = classService;
        }

        [HttpPost("public")]
        public async Task<IActionResult> GetViewModelsAsync([FromBody] ClassModel classModel)
        {
            var result = await _classService.GetViewModelsAsync(classModel);
            var listResult = new PaginatedList<ClassDto>(result.ToList(), result.Count(), classModel.PageIndex, classModel.PageSize);
            return Success(listResult);
        }

        //get project by id
        [HttpGet("{id}")]
        public async Task<IActionResult> GetClassById(Guid id)
        {
            var result = await _classService.GetByIdAsync(id);
            return Success(result);
        }

        //add project
        [HttpPost("add")]
        public async Task<IActionResult> AddClass([FromBody] CreateClassRequest addProjectModel)
        {
            await _classService.CreateAsync(addProjectModel);
            return Success();
        }

        //edit project
        [HttpPut("edit/{classId}")]
        public async Task<IActionResult> EditDetailClass(Guid classId, [FromBody] UpdateClassRequest editClassModel)
        {
            var result = await _classService.UpdateAsync(classId, editClassModel);
            return Success(result);
        }

        //delete project
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteClass(Guid id)
        {
            await _classService.DeleteAsync(id);
            return Success();
        }
    }
}
