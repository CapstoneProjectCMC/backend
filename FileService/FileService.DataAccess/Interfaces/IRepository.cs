using FileService.Core.ApiModels;
using FileService.DataAccess.Models;
using System;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;

namespace FileService.DataAccess.Interfaces
{
    public interface IRepository<T> where T : BaseDocument
    {
        Task<List<T>> GetAllActiveAsync();
        Task<IEnumerable<T>> GetAllAsync();
        Task<T> GetByIdAsync(Guid id);
        Task<IEnumerable<T>> FilterAsync(Expression<Func<T, bool>> predicate);
        Task<T> FindOneAsync(Expression<Func<T, bool>> predicate);
        Task<bool> AnyAsync(Expression<Func<T, bool>> predicate);
        Task<bool> ExistsAsync(Expression<Func<T, bool>> predicate);
        Task<T> CreateAsync(T document);
        Task UpdateAsync(T document);
        Task InsertManyAsync(IEnumerable<T> documents);
        Task DeleteAsync(Guid id); // soft delete
        Task DeleteManyAsync(Expression<Func<T, bool>> predicate);
        Task<IEnumerable<object>> GetDistinctAsync(string fieldName);
        Task<PaginatedList<T>> GetPagedListAsync(Expression<Func<T, bool>> predicate, int pageIndex, int pageSize);
    }
}

