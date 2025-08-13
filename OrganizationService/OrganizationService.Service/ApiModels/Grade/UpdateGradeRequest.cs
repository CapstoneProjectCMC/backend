using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.ApiModels.Grade
{
    public class UpdateGradeRequest
    {
        public string? GradeName { get; set; } 
        public string? Description { get; set; }
    }
}
