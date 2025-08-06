using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.DataAccess.Models
{
    public class Class : BaseTable<Guid>
    {
        [Required]
        public string Name { get; set; } = string.Empty;
        public string? Description { get; set; }
        public Grade Grade { get; set; }
        public Guid GradeId { get; set; }

       // public ICollection<OrganizationMember> OrganizationMembers { get; set; }
        public ICollection<StudentCredential> StudentCredentials { get; set; }
    }
}
