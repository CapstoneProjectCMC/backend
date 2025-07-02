using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace FileService.Core.Extensions
{
    public static class CustomAttributeExtensions
    {
        public static string GetHeaderName(this Type type, string propertyName)
        {
            var property = type.GetProperties().FirstOrDefault(p => string.Equals(p.Name, propertyName, StringComparison.OrdinalIgnoreCase));
            if (property == null)
            {
                return string.Empty;
            }

            var headerAttribute = property.GetCustomAttribute<HeaderNameAttribute>(true);

            return headerAttribute?.HeaderName ?? property.Name;
        }
    }
    public class HeaderNameAttribute : DisplayNameAttribute
    {
        public HeaderNameAttribute(string name) : base(name) { }
        public string HeaderName => DisplayName;
    }
}
