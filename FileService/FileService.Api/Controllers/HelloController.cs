using Microsoft.AspNetCore.Mvc;

namespace FileService.Api.Controllers
{
    [ApiController]
    [Route("/hello", Name = "GetHello")]
    public class HelloController
    {
        [HttpGet(Name = "GetHello")]
        public string GetHello()
        {
            return "Hello World From FILE SERVICE";
        }
    }
}

