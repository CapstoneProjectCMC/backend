using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.ApiModels.StudentCredential
{
    public class CreateStudentCredentialRequest
    {
        public Guid OrganizationId { get; set; }
        public Guid ClassId { get; set; }
        public string Username { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
    }

}
