using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using OrganizationService.Core.ApiModels;
using OrganizationService.Service.ApiModels.Grade;
using OrganizationService.Service.ApiModels.Organization;
using OrganizationService.Service.Dtos.Grade;
using OrganizationService.Service.Dtos.Organization;
using OrganizationService.Service.Implementation;
using OrganizationService.Service.Interfaces;

namespace OrganizationService.Api.Controllers
{
    [Authorize]
    [Authorize(Policy = "AdminOnly")]
    [Route("api/[controller]")]
    [ApiController]
    public class OrganizationController : BaseApiController
    {
        public IOrganizationService _organizationService { get; set; }
        private readonly IConfiguration _configuration;
        private readonly FileServiceClient _fileServiceClient;

        public OrganizationController(IServiceProvider serviceProvider, IOrganizationService organizationService, FileServiceClient fileServiceClient, IConfiguration configuration) : base(serviceProvider)
        {
            _configuration = configuration;
            _organizationService = organizationService;
            _fileServiceClient = fileServiceClient;
        }

        [HttpPost("public")]
        public async Task<IActionResult> GetViewModelsAsync([FromBody] OrganizationModel orgModel)
        {
            var result = await _organizationService.GetViewModelsAsync(orgModel);
            var listResult = new PaginatedList<OrganizationDto>(result.ToList(), result.Count(), orgModel.PageIndex, orgModel.PageSize);
            return Success(listResult);
        }

        //get organization by id
        [HttpGet("{id}")]
        public async Task<IActionResult> GetOrganizationById(Guid id)
        {
            var result = await _organizationService.GetByIdAsync(id);
            return Success(result);
        }

        //add organization
        [HttpPost("add")]
        public async Task<IActionResult> AddOrganization([FromForm] CreateOrganizationRequest addOrgModel)
        {
            var result = await _organizationService.CreateAsync(addOrgModel);
            return Success(result);
        }

        //edit organization
        [HttpPut("edit/{orgId}")]
        public async Task<IActionResult> EditDetailOrganization(Guid orgId, [FromForm] UpdateOrganizationRequest editOrgModel)
        {
            var result = await _organizationService.UpdateAsync(orgId, editOrgModel);
            return Success(result);
        }

        //delete organization
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteOrganization(Guid id)
        {
            var result = await _organizationService.DeleteAsync(id);
            return Success(result);
        }
    }
}
