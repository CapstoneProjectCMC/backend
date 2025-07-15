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
        Success = 0,

        [Description("System Error.")]
        Error = 1,

        [Description("Concurrency Conflict")]
        ConcurrencyConflict = 2,

        [Description("Không tìm thấy")]
        PageIndexInvalid = 3,

        [Description("Page Size Invalid")]
        PageSizeInvalid = 4,

        [Description("{Required Field} được yêu cầu.")]
        A01,

        [Description("{{Object}} không được tìm thấy")]
        A02,

        [Description("File với checksum giống nhau đã tồn tại.")]
        A03,

        [Description("Tên file không được để trống.")]
        A04,

        [Description("Tên file không được quá 255 ký tự.")]
        A05,

        [Description("Cần ít nhất một thẻ (tag).")]
        A06,

        [Description("Thể loại không được để trống")]
        A07,

        [Description("Tên file đã được dùng cho tài liệu khác.")]
        A08,

        [Description("Incorrect username or password. Please try again.")]
        B01,

        [Description("Download Interrupted. Please check your internet connection and try again.")]
        C05,

        [Description("Bad request.")]
        BadRequest,

        [Description("Invalid filter option.")]
        InvalidOption,

        [Description("Unmatched columns found.")]
        UnmatchedColumns,
    }
}
