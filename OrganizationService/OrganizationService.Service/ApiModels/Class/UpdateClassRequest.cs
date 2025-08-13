using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.ApiModels.Class
{
    public class UpdateClassRequest
    {
        public string? Name { get; set; }
        public string? Description { get; set; }
        public Guid? GradeId { get; set; }
    }
}
