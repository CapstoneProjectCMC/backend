using OrganizationService.Core.Constants;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.DataAccess.Models
{
    public class Organization : BaseTable<Guid>
    {
        [Required]
        public string Name { get; set; } = string.Empty;
        public string? Description { get; set; }
        public string? Address { get; set; }
        public string? Email { get; set; }

        [MaxLength(10)]
        [RegularExpression("^[0-9]*$")]
        public string? Phone { get; set; }
        public Guid? Logo { get; set; }
        public string? LogoUrl { get; set; }
        public OrganizationStatus Status { get; set; }
        public ICollection<Grade> Grades { get; set; } 
        public ICollection<OrganizationMember> OrganizationMembers { get; set; }
        public ICollection<StudentCredential> StudentCredentials { get; set; }
    }
}
