using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.DataAccess.Models
{
    public class AuditChange : BaseTable<Guid>
    {
        [Required]
        public string EntityName { get; set; }

        [Required]
        public string Action { get; set; }

        [Required]
        public string RecordId { get; set; }

        public string? ColumnEffect { get; set; }

        public string? OldValue { get; set; }

        public string? NewValue { get; set; }

        public Guid UserId { get; set; }

        public Guid? ActionId { get; set; }

    }
}
