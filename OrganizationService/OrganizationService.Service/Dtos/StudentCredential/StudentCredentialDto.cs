using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.Dtos.StudentCredential
{
    public class StudentCredentialDto
    {
        public Guid Id { get; set; }
        public Guid OrganizationId { get; set; }
        public Guid ClassId { get; set; }
        public string Username { get; set; } = string.Empty;
        public bool IsUsed { get; set; }
    }

}
