using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.DataAccess.Models
{
    public class BaseTable
    {
        public bool IsDeleted { get; set; }
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime? UpdatedAt { get; set; }
        public Guid CreatedBy { get; set; }
        public Guid? UpdatedBy { get; set; }
        [Timestamp]
        public byte[] RowVersion { get; set; }
    }
}
