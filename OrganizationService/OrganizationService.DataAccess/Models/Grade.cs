using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.DataAccess.Models
{
    public class Grade : BaseTable<Guid>
    {
        [Required]
        public string Name { get; set; } = string.Empty;
        public string? Description { get; set; }
        public Guid OrganizationId { get; set; }
        public Organization Organization { get; set; } 
        public ICollection<Class> Classes { get; set; }
    }
}
