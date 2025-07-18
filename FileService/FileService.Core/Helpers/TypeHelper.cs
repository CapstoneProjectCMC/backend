using FileService.Core.Enums;
using FileService.Core.Exceptions;
using FileService.Core.Extensions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileService.Core.Helpers
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

                    // change column name to correct format
                    var formattedColumnName = char.ToUpper(matchingProperty[0]) + matchingProperty.Substring(1);
                    verifiedProperties.Add(formattedColumnName, string.IsNullOrEmpty(displayName) ? type.GetHeaderName(matchingProperty) : displayName);
                }
                else
                {
                    unmatchedColumns.Add(colName);
                }
            }

            //check fields is not double and throw exception
            if (unmatchedColumns.Any())
            {
                throw new ErrorException(StatusCodeEnum.UnmatchedColumns);
            }

            return verifiedProperties;
        }
    }
}
