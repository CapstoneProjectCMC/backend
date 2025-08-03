using OrganizationService.Core.Enums;
using OrganizationService.Core.Exceptions;
using OrganizationService.Core.Extensions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Core.Helpers
{
    public static class TypeHelper
    {
        public static Dictionary<string, string> VerifyProperties<T>(Dictionary<string, string> propertiesWithDisplayName) where T : class
        {
            var type = typeof(T);
            var verifiedProperties = new Dictionary<string, string>();
            var propertyNames = type.GetProperties().Select(p => p.Name).ToArray();
            var unmatchedColumns = new List<string>();

            foreach (var colName in propertiesWithDisplayName.Keys)
            {
                var matchingProperty = propertyNames.FirstOrDefault(x => string.Equals(colName, x, StringComparison.OrdinalIgnoreCase));
                if (matchingProperty != null)
                {
                    var displayName = propertiesWithDisplayName[colName];

                    // Chuyển đổi tên cột về định dạng đúng
                    var formattedColumnName = char.ToUpper(matchingProperty[0]) + matchingProperty.Substring(1);
                    verifiedProperties.Add(formattedColumnName, string.IsNullOrEmpty(displayName) ? type.GetHeaderName(matchingProperty) : displayName);
                }
                else
                {
                    unmatchedColumns.Add(colName);
                }
            }

            // Kiểm tra các cột không trùng và throw exception
            if (unmatchedColumns.Any())
            {
                throw new ErrorException(StatusCodeEnum.UnmatchedColumns);
            }

            return verifiedProperties;
        }
    }
}
