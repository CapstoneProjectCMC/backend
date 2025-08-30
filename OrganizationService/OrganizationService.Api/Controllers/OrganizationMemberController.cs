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
        
        // v2: nhận string scopeType/role để tương thích Identity
        [HttpPost("add-v2")]
        public async Task<IActionResult> AddMemberV2([FromBody] CreateOrganizationMemberV2Request request)
        {
            var scopeParsed = Enum.TryParse<OrganizationService.Core.Constants.ScopeType>(
                request.ScopeType, true, out var scopeType)
                ? scopeType
                : OrganizationService.Core.Constants.ScopeType.Organization;

            // Chuẩn hóa role string: ADMIN/TEACHER/STUDENT => Admin/Teacher/Student
            string NormalizeRole(string s) => s?.Trim() switch
            {
                null or "" => "Student",
                var v when v.Equals("SUPERADMIN", StringComparison.OrdinalIgnoreCase) => "SuperAdmin",
                var v when v.Equals("ADMIN", StringComparison.OrdinalIgnoreCase) => "Admin",
                var v when v.Equals("TEACHER", StringComparison.OrdinalIgnoreCase) => "Teacher",
                var v when v.Equals("STUDENT", StringComparison.OrdinalIgnoreCase) => "Student",
                _ => request.Role
            };

            var roleParsed = Enum.TryParse<OrganizationService.Core.Constants.MemberRole>(
                NormalizeRole(request.Role), true, out var role)
                ? role
                : OrganizationService.Core.Constants.MemberRole.Student;

            var dto = new CreateOrganizationMemberRequest
            {
                UserId = request.UserId,
                ScopeType = scopeParsed,
                ScopeId = request.ScopeId,
                Role = roleParsed,
                IsActive = request.IsActive
            };

            await _orgMemberService.CreateOrUpdateAsync(dto);
            return Success();
        }

        // bulk: tối ưu cho import excel
        [HttpPost("bulk-add")]
        public async Task<IActionResult> BulkAdd([FromBody] IEnumerable<CreateOrganizationMemberV2Request> list)
        {
            IEnumerable<CreateOrganizationMemberRequest> mapped = list.Select(r =>
            {
                Enum.TryParse<OrganizationService.Core.Constants.ScopeType>(r.ScopeType, true, out var sType);
                // dùng NormalizeRole như trên
                string NormalizeRoleLocal(string x) => x?.Trim() switch
                {
                    null or "" => "Student",
                    var v when v.Equals("SUPERADMIN", StringComparison.OrdinalIgnoreCase) => "SuperAdmin",
                    var v when v.Equals("ADMIN", StringComparison.OrdinalIgnoreCase) => "Admin",
                    var v when v.Equals("TEACHER", StringComparison.OrdinalIgnoreCase) => "Teacher",
                    var v when v.Equals("STUDENT", StringComparison.OrdinalIgnoreCase) => "Student",
                    _ => x
                };
                Enum.TryParse<OrganizationService.Core.Constants.MemberRole>(NormalizeRoleLocal(r.Role), true, out var mRole);
                return new CreateOrganizationMemberRequest
                {
                    UserId = r.UserId,
                    ScopeType = sType,
                    ScopeId = r.ScopeId,
                    Role = mRole,
                    IsActive = r.IsActive
                };
            }).ToList();

            var result = await _orgMemberService.BulkCreateOrUpdateAsync(mapped);
            return Success(new PaginatedList<OrganizationMemberDto>(result.ToList(), result.Count(), 1, result.Count()));
        }

    }
}
