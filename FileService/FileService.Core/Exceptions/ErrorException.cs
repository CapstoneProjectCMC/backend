using FileService.Core.Enums;
using FileService.Core.Extensions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileService.Core.Exceptions
{
    public class ErrorException : Exception
    {
        public StatusCodeEnum StatusCode { get; set; }
        public object Data { get; set; }

        public ErrorException(StatusCodeEnum statusCode) : base(statusCode.GetDescription())
        {
            StatusCode = statusCode;
        }

        public ErrorException(StatusCodeEnum statusCode, object data) : base(statusCode.GetDescription())
        {
            StatusCode = statusCode;
            Data = data;
        }

        public ErrorException(StatusCodeEnum statusCode, string customMessage) : base(customMessage)
        {
            StatusCode = statusCode;
        }

        public ErrorException(string message) : base(message) { }
    }
}
