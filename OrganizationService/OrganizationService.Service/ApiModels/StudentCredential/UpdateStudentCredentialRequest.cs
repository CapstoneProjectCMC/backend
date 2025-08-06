using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.ApiModels.StudentCredential
{
    public class UpdateStudentCredentialRequest
    {
        public string? Password { get; set; }
        public bool? IsUsed { get; set; }
    }

}
