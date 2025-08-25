using OrganizationService.Core.Enums;
using OrganizationService.Core.Extensions;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Core.ApiModels
{
    public class ApiResponseModel
    {
        public ApiResponseModel()
        {
            Code = (int)StatusCodeEnum.Success;
            Message = GetDescription(StatusCodeEnum.Success);
            Status = "success";
        }

        public ApiResponseModel(object data) : this()
        {
            Result = data;
        }
        
        public ApiResponseModel(StatusCodeEnum status, object? data = null)
        {
            Code = (int)status;
            Message = GetDescription(status);
            Status = status == StatusCodeEnum.Success ? "success" : "fail";
            Result = data;
        }

        public int Code { get; set; }

        public string Message { get; set; } = string.Empty;

        public string Status { get; set; } = "success";

        public object? Result { get; set; }
        private static string GetDescription(StatusCodeEnum value)
        {
            var fi = value.GetType().GetField(value.ToString());
            if (fi != null)
            {
                var attributes = (DescriptionAttribute[])fi.GetCustomAttributes(
                    typeof(DescriptionAttribute), false);

                if (attributes.Length > 0)
                    return attributes[0].Description;
            }
            return value.ToString();
        }
    }
    public class ApiResponseModel<T>
    {
        public ApiResponseModel()
        {
            Code = (int)StatusCodeEnum.Success;
            Message = GetDescription(StatusCodeEnum.Success);
            Status = "success";
        }

        public ApiResponseModel(T data) : this()
        {
            Result = data;
        }

        public ApiResponseModel(StatusCodeEnum status, T? data = default)
        {
            Code = (int)status;
            Message = GetDescription(status);
            Status = status == StatusCodeEnum.Success ? "success" : "fail";
            Result = data;
        }

        public int Code { get; set; }
        public string Message { get; set; } = string.Empty;
        public string Status { get; set; } = "success";
        public T? Result { get; set; }
        private static string GetDescription(StatusCodeEnum value)
        {
            var fi = value.GetType().GetField(value.ToString());
            if (fi != null)
            {
                var attributes = (DescriptionAttribute[])fi.GetCustomAttributes(
                    typeof(DescriptionAttribute), false);

                if (attributes.Length > 0)
                    return attributes[0].Description;
            }
            return value.ToString();
        }
    }
}
