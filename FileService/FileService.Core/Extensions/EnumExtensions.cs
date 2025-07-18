using System.ComponentModel;

namespace FileService.Core.Extensions
{
    public static class EnumExtension
    {
        public static string GetDescription<TEnum>(this TEnum value) where TEnum : struct
        {
            object[] customAttributes = typeof(TEnum).GetMember(value.ToString())[0].GetCustomAttributes(typeof(DescriptionAttribute), inherit: false);
            if (customAttributes != null && customAttributes.Length != 0)
            {
                return (customAttributes[0] as DescriptionAttribute).Description;
            }

            return string.Empty;
        }
    }
}
