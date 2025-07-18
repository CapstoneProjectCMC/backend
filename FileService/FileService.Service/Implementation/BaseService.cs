using FileService.Core.ApiModels;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileService.Service.Implementation
{
    public class BaseService
    {
        protected AppSettings _appSettings { get; set; }
        protected UserContext _userContext { get; set; }

        public BaseService(AppSettings appSettings, UserContext userContext)
        {
            _appSettings = appSettings;
            _userContext = userContext;
        }
    }
}
