using FileService.Core.Enums;
using FileService.Core.Extensions;
using Microsoft.AspNetCore.Http;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace FileService.Core.ApiModels
{
    public class ApiResponseModel
    {
        public ApiResponseModel()
        {
            Code = (int)StatusCodeEnum.Success;
            Message = GetDescription(StatusCodeEnum.Success);
            Status = "success";
            Result = null;
        }
        public ApiResponseModel(object data)
        {
            Code = (int)StatusCodeEnum.Success;
            Data = data;
        }
        public ApiResponseModel(StatusCodeEnum status)
        {
            Code = (int)status;
            Message = GetDescription(status);
            Status = status == StatusCodeEnum.Success ? "success" : "fail";
            Result = null;
        }
        public ApiResponseModel(StatusCodeEnum status, object? data)
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
        public object? Data { get; set; }
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
