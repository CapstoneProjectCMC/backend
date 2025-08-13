using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.ApiModels.Class
{
    public class CreateClassRequest
    {
        [Required]
        public string ClassName { get; set; } = string.Empty;
        public string? Description { get; set; }

        [Required]
        public Guid GradeId { get; set; }
        public string GradeName { get; set; } = string.Empty;
    }
}
