using Microsoft.AspNetCore.Mvc;
using OrganizationService.Core.ApiModels;
using OrganizationService.Core.Enums;

namespace OrganizationService.Api.Controllers
{
    public class BaseApiController : Controller
    {
        protected readonly AppSettings _appSettings;

        public BaseApiController(IServiceProvider serviceProvider)
        {

            _appSettings = serviceProvider.GetRequiredService<AppSettings>();
        }

        [NonAction]
        public IActionResult Success<T>(T data)
        {
            var response = new ApiResponseModel<T>(StatusCodeEnum.Success, data);
            return Ok(response);
        }

        [NonAction]
        public IActionResult Fail(StatusCodeEnum errorCode)
        {
            var response = new ApiResponseModel(errorCode);
            return BadRequest(response);
        }

        [NonAction]
        public IActionResult Success(object? data = null)
        {
            return Result(new ApiResponseModel(data));
        }

        [NonAction]
        public IActionResult Result(StatusCodeEnum code)
        {
            return Result(new ApiResponseModel(code));
        }

        [NonAction]
        public IActionResult Result(ApiResponseModel resp)
        {
            return Ok(resp);
        }

        [NonAction]
        public IActionResult BadResult(bool withModelState)
        {
            IActionResult result = BadRequest();
            if (withModelState)
            {
                result = BadRequest(ModelState);
            }

            return result;
        }

        [NonAction]
        public IActionResult BadResult(ApiResponseModel message)
        {

            return BadRequest(message);
        }
    }
}
