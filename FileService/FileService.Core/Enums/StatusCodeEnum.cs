using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace FileService.Core.Enums
{
    public enum StatusCodeEnum
    {
        [Description("Lỗi hệ thống.")]
        Error = 5008201,

        [Description("Xung đột đồng thời")]
        ConcurrencyConflict = 4098202,

        [Description("Không tìm thấy")]
        PageIndexInvalid = 4048203,

        [Description("Kích thước trang không hợp lệ")]
        PageSizeInvalid = 4008204,

        [Description("{Required Field} được yêu cầu.")]
        A01 = 4008205,

        [Description("{{Object}} không được tìm thấy")]
        A02 = 4048206,

        [Description("Tên file không được để trống.")]
        A04 = 4008208,

        [Description("Tên file không được quá 255 ký tự.")]
        A05 = 4008209,

        [Description("Cần ít nhất một thẻ (tag).")]
        A06 = 4008210,

        [Description("Thể loại không được để trống")]
        A07 = 4008211,

        [Description("Tên file đã được dùng cho tài liệu khác.")]
        A08 = 4098212,

        [Description("Incorrect username or password. Please try again.")]
        B01 = 4018213,

        [Description("Download Interrupted. Please check your internet connection and try again.")]
        C05 = 5038214,

        [Description("Invalid filter option.")]
        InvalidOption = 4008216,

        [Description("Unmatched columns found.")]
        UnmatchedColumns = 4008217,

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

        [Description("Không tìm thấy tài nguyên hoặc sai định dạng, không truy vấn được")]
        D01 = 4008218,

        [Description("No authentication token provided")]
        D02 = 4018219,
    }
}
