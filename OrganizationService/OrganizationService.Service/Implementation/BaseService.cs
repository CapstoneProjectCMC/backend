using OrganizationService.Core.ApiModels;
using OrganizationService.DataAccess.Interfaces;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.Implementation
{
    public class BaseService
    {
        protected AppSettings _appSettings { get; set; }
        protected IUnitOfWork _unitOfWork { get; set; }
        protected UserContext _userContext { get; set; }

        public BaseService(AppSettings appSettings, IUnitOfWork unitOfWork, UserContext userContext)
        {
            _appSettings = appSettings;
            _unitOfWork = unitOfWork;
            _userContext = userContext;
        }
    }
}
