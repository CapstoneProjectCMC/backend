using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileService.Core.ApiModels
{
    public class PagingBaseModel
    {
        [DefaultValue(1)]
        public int PageIndex { get; set; } 
        [DefaultValue(20)]
        public int PageSize { get; set; } 
    }
}
