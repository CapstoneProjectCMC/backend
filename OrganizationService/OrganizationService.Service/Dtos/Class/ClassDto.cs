using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.Dtos.Class
{
    public class ClassDto
    {
        public Guid Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public string? Description { get; set; }
        public Guid GradeId { get; set; }
        public string GradeName { get; set; } = string.Empty;
    }
}
