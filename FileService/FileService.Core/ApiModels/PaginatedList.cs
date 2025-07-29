using FileService.Core.Enums;
using FileService.Core.Exceptions;
using MongoDB.Driver.Linq;
using System;
using System.Text;
using System.Threading.Tasks;

namespace FileService.Core.ApiModels
{
    public class PaginatedList<T>
    {
        public IReadOnlyCollection<T> Datas{ get; }
        public int CurrentPage { get; }
        public int TotalPages { get; }
        public int PageSize { get; } 
        public int TotalElements { get; }

        public PaginatedList(IReadOnlyCollection<T> items, int count, int currentPage, int pageSize)
        {
            if (currentPage < 0)
            {
                throw new ErrorException(StatusCodeEnum.PageIndexInvalid);
            }

            if (pageSize < 0)
            {
                throw new ErrorException(StatusCodeEnum.PageSizeInvalid);
            }

            CurrentPage = currentPage;
            PageSize = pageSize;
            TotalPages = (int)Math.Ceiling(count / (double)pageSize);
            TotalElements = count;
            Datas = items.Skip((currentPage - 1) * pageSize).Take(pageSize).ToList();
        }

        public bool HasPreviousPage => CurrentPage > 1;

        public bool HasNextPage => CurrentPage < TotalPages;

        public static async Task<PaginatedList<T>> CreateAsync(IQueryable<T> source, int currentPage, int pageSize)
        {
            if (currentPage < 1)
            {
                throw new ErrorException(StatusCodeEnum.PageIndexInvalid);
            }

            if (pageSize < 0)
            {
                throw new ErrorException(StatusCodeEnum.PageSizeInvalid);
            }

            var count = await source.CountAsync();
            var items = await source.Skip((currentPage - 1) * pageSize).Take(pageSize).ToListAsync();
            return new PaginatedList<T>(items, count, currentPage, pageSize);
        }

        public static PaginatedList<T> Create(List<T> source, int currentPage, int pageSize)
        {
            if (currentPage < 1)
            {
                throw new ErrorException(StatusCodeEnum.PageIndexInvalid);
            }

            if (pageSize < 0)
            {
                throw new ErrorException(StatusCodeEnum.PageSizeInvalid);
            }

            var count = source.Count();
            var items = source.Skip((currentPage - 1) * pageSize).Take(pageSize).ToList();
            return new PaginatedList<T>(items, count, currentPage, pageSize);
        }
    }
}
