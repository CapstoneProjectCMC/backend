using Microsoft.EntityFrameworkCore;
using OrganizationService.Core.ApiModels;
using OrganizationService.Core.Enums;
using OrganizationService.Core.Exceptions;
using OrganizationService.DataAccess.DbContexts;
using OrganizationService.DataAccess.Interfaces;
using OrganizationService.DataAccess.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.DataAccess.Implementation
{
    public class Repository<T> : IRepository<T> where T : BaseTable<Guid>
    {
        protected readonly OrganizationServiceDbContext _dbContext;
        protected readonly UserContext _userContext;

        public IQueryable<T> ActiveRecords
        {
            get
            {
                return _dbContext.Set<T>().Where(en => en.IsDeleted == false);
            }
        }

        public IQueryable<T> AsNoTracking
        {
            get
            {
                return _dbContext.Set<T>().AsNoTracking().Where(en => !en.IsDeleted);
            }
        }

        public IQueryable<T> ActiveRecordsWithoutSoftDelete
        {
            get
            {
                return _dbContext.Set<T>();
            }
        }

        public IQueryable<T> AsNoTrackingWithoutSoftDelete
        {
            get
            {
                return _dbContext.Set<T>().AsNoTracking();
            }
        }

        public Repository(OrganizationServiceDbContext dbContext, UserContext userContext)
        {
            _dbContext = dbContext;
            _userContext = userContext;
        }

        public async Task<bool> AnyAsync(Expression<Func<T, bool>> predicate)
        {
            return await ActiveRecords.AnyAsync(predicate);
        }

        public async Task<IEnumerable<T>> FilterAsync(Expression<Func<T, bool>> predicate)
        {
            return await ActiveRecords.Where(predicate).ToListAsync();
        }

        public async Task<IEnumerable<T>> FindElementAsync(Expression<Func<T, bool>> predicate)
        {
            return await _dbContext.Set<T>().AsNoTracking().Where(predicate).ToListAsync();
        }

        public async Task<IEnumerable<object>> GetDistinctColumnAsync(string columnName)
        {
            var parameter = Expression.Parameter(typeof(T), "x");
            var propertyInfo = typeof(T).GetProperties()
                .FirstOrDefault(p => p.Name.Equals(columnName, StringComparison.OrdinalIgnoreCase));
            if (propertyInfo == null)
            {
                throw new ErrorException(StatusCodeEnum.BadRequest);
            }
            var property = Expression.Property(parameter, propertyInfo);
            var propertyType = propertyInfo.PropertyType;

            LambdaExpression lambda = propertyType switch
            {
                Type t when t == typeof(string) => Expression.Lambda<Func<T, string>>(property, parameter),
                Type t when t == typeof(bool?) => Expression.Lambda<Func<T, object>>(Expression.Convert(property, typeof(object)), parameter),
                Type t when t.IsEnum => Expression.Lambda<Func<T, string>>(Expression.Call(property, "ToString", null), parameter),
                Type t when t == typeof(DateTime?) => Expression.Lambda<Func<T, string>>(
                    Expression.Call(
                        Expression.Convert(property, typeof(DateTime)), // Convert to DateTime
                        typeof(DateTime).GetMethod("ToString", new[] { typeof(string) }),
                        Expression.Constant("hh:mm tt")
                    ),
                    parameter
                    ),
                _ => throw new ErrorException(StatusCodeEnum.BadRequest)
            };

            var query = _dbContext.Set<T>().Where(en => !en.IsDeleted);

            var result = propertyType switch
            {
                Type t when t == typeof(string) => await query.Select((Expression<Func<T, string>>)lambda).Distinct().ToListAsync<object>(),
                Type t when t == typeof(bool?) => await query.Select((Expression<Func<T, object>>)lambda).Distinct().ToListAsync(),
                Type t when t.IsEnum => await query.Select((Expression<Func<T, string>>)lambda).Distinct().ToListAsync<object>(),
                Type t when t == typeof(DateTime?) => await query.Select((Expression<Func<T, string>>)lambda).Distinct().ToListAsync<object>(),
                _ => throw new ErrorException(StatusCodeEnum.BadRequest)
            };
            return result.Distinct();
        }
        public async Task<IEnumerable<T>> GetListAsync()
        {
            return await ActiveRecords.ToListAsync();
        }

        public async Task<T> FindByIdAsync(Guid id)
        {
            return await ActiveRecords.FirstOrDefaultAsync(en => en.Id == id);
        }

        public async Task<T> FindAsync(Expression<Func<T, bool>> predicate)
        {
            return await ActiveRecords.Where(predicate).FirstOrDefaultAsync();
        }

        public async Task InsertAsync(T obj)
        {
            obj.CreatedBy = _userContext.UserId;
            obj.CreatedAt = DateTime.UtcNow;

            await _dbContext.Set<T>().AddAsync(obj);
        }

        public void Update(T obj)
        {
            obj.UpdatedAt = DateTime.UtcNow;
            obj.UpdatedBy = _userContext.UserId;

            _dbContext.Set<T>().Update(obj);
        }

        public void UpdateMany(IEnumerable<T> objs)
        {
            foreach (T obj in objs)
            {
                obj.UpdatedAt = DateTime.UtcNow;
                obj.UpdatedBy = _userContext.UserId;
            }

            _dbContext.Set<T>().UpdateRange(objs);
        }

        public void Delete(T obj)
        {
            obj.DeletedAt = DateTime.UtcNow;
            obj.DeletedBy = _userContext.UserId;
            obj.IsDeleted = true;

            _dbContext.Set<T>().Update(obj);
        }

        public async Task DeleteAsync(Guid id)
        {
            var entity = await _dbContext.Set<T>()
                .FirstOrDefaultAsync(x => x.Id == id && !x.IsDeleted);

            if (entity == null)
                throw new KeyNotFoundException("Không tìm thấy bản ghi hoặc đã bị xóa.");

            entity.DeletedAt = DateTime.UtcNow;
            entity.DeletedBy = _userContext.UserId;
            entity.IsDeleted = true;

            await _dbContext.SaveChangesAsync();
        }

        public async Task<T> DeleteByIdAsync(Guid id)
        {
            var obj = await FindByIdAsync(id);
            if (obj != null)
            {
                Delete(obj);
            }

            return obj;
        }
        public async Task<T> CreateAsync(T obj)
        {
            obj.CreatedBy = _userContext.UserId;

            var result = await _dbContext.Set<T>().AddAsync(obj);

            return result.Entity;
        }

        public Task<PaginatedList<T>> GetPagedListAsync(
            Expression<Func<T, bool>> predicate = null,
             int pageIndex = 0,
             int pageSize = 20
            )
        {
            var queryable = AsNoTracking.Where(predicate);
            return PaginatedList<T>.CreateAsync(queryable, pageSize, pageIndex);
        }

        public async Task DeleteManyAsync(Expression<Func<T, bool>> predicate)
        {
            var records = await ActiveRecords.Where(predicate).ToListAsync();

            foreach (var record in records)
            {
                record.UpdatedAt = DateTime.UtcNow;
                record.UpdatedBy = _userContext.UserId;
                record.IsDeleted = true;
            }

            _dbContext.Set<T>().UpdateRange(records);
        }
    }
}
