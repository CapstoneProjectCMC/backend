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
        public int PageIndex { get; set; } = 1;
        [DefaultValue(50)]
        public int PageSize { get; set; } = 50;
    }
}
