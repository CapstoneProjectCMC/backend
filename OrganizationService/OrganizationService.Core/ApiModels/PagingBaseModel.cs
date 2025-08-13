using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Core.ApiModels
{
    public class PagingBaseModel
    {
        [DefaultValue(1)]
        public int PageIndex { get; set; } 
        [DefaultValue(30)]
        public int PageSize { get; set; } 
    }
}
