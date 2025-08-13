using OrganizationService.Core.ApiModels;
using OrganizationService.DataAccess.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Threading.Tasks;

namespace OrganizationService.DataAccess.Interfaces
{
    public interface IRepository<T> where T : BaseTable<Guid>
    {
        IQueryable<T> ActiveRecords { get; }
        IQueryable<T> AsNoTracking { get; }
        IQueryable<T> ActiveRecordsWithoutSoftDelete { get; }
        IQueryable<T> AsNoTrackingWithoutSoftDelete { get; }
        Task<IEnumerable<T>> FilterAsync(Expression<Func<T, bool>> predicate);
        Task<bool> AnyAsync(Expression<Func<T, bool>> predicate);
        Task<IEnumerable<T>> GetListAsync();
        Task<T> FindByIdAsync(Guid id);
        Task<T> FindAsync(Expression<Func<T, bool>> predicate);
        void Update(T obj);
        void UpdateMany(IEnumerable<T> objs);
        Task InsertAsync(T obj);
        void Delete(T obj);
        Task DeleteAsync(Guid id);
        Task<T> DeleteByIdAsync(Guid id);
        Task<T> CreateAsync(T obj);
        Task<PaginatedList<T>> GetPagedListAsync(
            Expression<Func<T, bool>> predicate = null,
             int pageIndex = 0,
             int pageSize = 30
            );
        Task DeleteManyAsync(Expression<Func<T, bool>> predicate);
        Task<IEnumerable<T>> FindElementAsync(Expression<Func<T, bool>> predicate);
        Task<IEnumerable<object>> GetDistinctColumnAsync(string columnName);
    }
}
