using OrganizationService.Core.Constants;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.DataAccess.Models
{
    public class OrganizationMember : BaseTable<Guid>
    {
        public Guid UserId { get; set; }
        public ScopeType ScopeType { get; set; } // Organization, Grade, Class
        public Guid ScopeId { get; set; } // id của tổ chức, khối, lớp tương ứng
        public MemberRole Role { get; set; } // superAdmin, admin, teacher, student
        public bool IsActive { get; set; }
       
    }
}
