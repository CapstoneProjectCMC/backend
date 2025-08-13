using OrganizationService.Core.ApiModels;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Service.ApiModels.StudentCredential
{
    public class StudentCredentialModel : PagingBaseModel
    {
        public Guid? OrganizationId { get; set; }
        public Guid? GradeId { get; set; }
        public Guid? ClassId { get; set; }
        public bool? IsUsed { get; set; }
    }
}
