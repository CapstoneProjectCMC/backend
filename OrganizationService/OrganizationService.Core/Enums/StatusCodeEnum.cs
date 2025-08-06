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
        [Description("Thành công.")]
        Success = 2008200,

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

        [Description("File với checksum giống nhau đã tồn tại.")]
        A03 = 4098207,

        [Description("Incorrect username or password. Please try again.")]
        B01 = 4018213,

        [Description("Download Interrupted. Please check your internet connection and try again.")]
        C05 = 5038214,

        [Description("Bad request.")]
        BadRequest = 4008215,

        [Description("Invalid filter option.")]
        InvalidOption = 4008216,

        [Description("Unmatched columns found.")]
        UnmatchedColumns = 4008217,
    }
}
