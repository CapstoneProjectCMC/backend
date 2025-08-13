using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.DataAccess.Models
{
    public class StudentCredential : BaseTable<Guid>
    {
        public Guid OrganizationId { get; set; }
        public Guid ClassId { get; set; }
        public string Username { get; set; } = string.Empty;
        public string PasswordHashed { get; set; } = string.Empty;
        public bool IsUsed { get; set; }

        public Organization Organization { get; set; }
        public Class Class { get; set; }
    }
}
