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

        [Description("Not Found")]
        PageIndexInvalid = 3,

        [Description("Page Size Invalid")]
        PageSizeInvalid = 4,

        [Description("{Required Field} is required.")]
        A01,

        [Description("Incorrect username or password. Please try again.")]
        B01,

        [Description("{{Object}} not found")]
        A02,

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
