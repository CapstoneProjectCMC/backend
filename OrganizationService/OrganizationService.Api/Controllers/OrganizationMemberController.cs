using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using OrganizationService.Core.ApiModels;
using OrganizationService.Service.ApiModels.Class;
using OrganizationService.Service.ApiModels.OrganizationMember;
using OrganizationService.Service.Dtos.Class;
using OrganizationService.Service.Dtos.OrganizationMember;
using OrganizationService.Service.Interfaces;

namespace OrganizationService.Api.Controllers
{
    [AllowAnonymous]    
    [Route("api/[controller]")]
    [ApiController]
    public class OrganizationMemberController : BaseApiController
    {
        public IOrganizationMemberService _orgMemberService { get; set; }
        private readonly IConfiguration _configuration;

        public OrganizationMemberController(IServiceProvider serviceProvider, IOrganizationMemberService orgMemberService, IConfiguration configuration) : base(serviceProvider)
        {
            _configuration = configuration;
            _orgMemberService = orgMemberService;
        }

        [HttpPost("public")]
        public async Task<IActionResult> GetViewModelsAsync([FromBody] OrganizationMemberModel orgMemberModel)
        {
            var result = await _orgMemberService.GetViewModelsAsync(orgMemberModel);
            var listResult = new PaginatedList<OrganizationMemberDto>(result.ToList(), result.Count(), orgMemberModel.PageIndex, orgMemberModel.PageSize);
            return Success(listResult);
        }

        //get project by id
        [HttpGet("{id}")]
        public async Task<IActionResult> GetOrganizationMemberById(Guid id)
        {
            var result = await _orgMemberService.GetByIdAsync(id);
            return Success(result);
        }

        //add project
        [HttpPost("add")]
        public async Task<IActionResult> AddClass([FromBody] CreateOrganizationMemberRequest addOrgMemberModel)
        {
            await _orgMemberService.CreateAsync(addOrgMemberModel);
            return Success();
        }

        //edit project
        [HttpPut("edit/{classId}")]
        public async Task<IActionResult> EditDetailClass(Guid classId, [FromBody] UpdateOrganizationMemberRequest editOrgMemberModel)
        {
            var result = await _orgMemberService.UpdateAsync(classId, editOrgMemberModel);
            return Success(result);
        }

        //delete project
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteOrganizationMember(Guid id)
        {
            await _orgMemberService.DeleteAsync(id);
            return Success();
        }
        
        [HttpGet("user/{userId:guid}/primary")]
        public async Task<IActionResult> GetPrimaryOrg(Guid userId)
        {
            var result = await _orgMemberService.GetPrimaryOrgForUser(userId);
            return Success(result);
        }

        [HttpPost("join/{organizationId:guid}")]
        public async Task<IActionResult> JoinOrganization(Guid organizationId)
        {
            var result = await _orgMemberService.JoinOrganizationForCurrentUser(organizationId);
            return Success(result);
        }

        [HttpPost("{organizationId:guid}/admin/grant/{userId:guid}")]
        public async Task<IActionResult> GrantAdmin(Guid organizationId, Guid userId)
        {
            var result = await _orgMemberService.GrantAdmin(organizationId, userId);
            return Success(result);
        }
    }
}
