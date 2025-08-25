using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.ApiModels.Organization
{
    public class FileUploadResult
    {
        public Guid FileId { get; set; } 
        public string Url { get; set; } = string.Empty;
    }
}
