using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.Core.Enums
{
    public enum StatusCodeEnum
    {
        [Description("Lỗi hệ thống.")]
        Error = 5008301,

        [Description("Xung đột đồng thời")]
        ConcurrencyConflict = 4098302,

        [Description("Không tìm thấy")]
        PageIndexInvalid = 4048303,

        [Description("Kích thước trang không hợp lệ")]
        PageSizeInvalid = 4008304,

        [Description("{Required Field} được yêu cầu.")]
        A01 = 4008305,

        [Description("{{Object}} không được tìm thấy")]
        A02 = 4048306,

        [Description("Incorrect username or password. Please try again.")]
        B01 = 4018313,

        [Description("Download Interrupted. Please check your internet connection and try again.")]
        C05 = 5038314,

        [Description("Invalid filter option.")]
        InvalidOption = 4008316,

        [Description("Unmatched columns found.")]
        UnmatchedColumns = 4008317,

        [Description("Success")]
        Success = 200,

        [Description("Bad Request")]
        BadRequest = 400,

        [Description("Unauthorized")]
        Unauthorized = 401,

        [Description("Forbidden")]
        Forbidden = 403,

        [Description("Not Found")]
        NotFound = 404,

        [Description("Internal Server Error")]
        InternalServerError = 500,

        [Description("Organization not found")]
        D01 = 4008320,

        [Description("Organization with this name already exists")]
        D02 = 4008321,

        [Description("Failed to upload logo")]
        D03 = 4038322,

        [Description("No authentication token provided for File Service.")]
        D04 = 4038323,

        [Description("Logo file is empty or null")]
        D05 = 4038324,

        [Description("Invalid file format. Only PNG, JPG, JPEG, WEBP, GIF are allowed.")]  
        D06 = 4008325,

        [Description("Logo file is too large.")]
        D07 = 4008326,

        [Description("File Service did not return a valid result.")]
        D08 = 5008327
    }
}
