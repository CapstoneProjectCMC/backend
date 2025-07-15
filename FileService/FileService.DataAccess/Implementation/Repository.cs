using FileService.Core.ApiModels;
using FileService.DataAccess.Interfaces;
using FileService.DataAccess.Models;
using MongoDB.Driver;
using System;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;

namespace FileService.DataAccess.Implementation
{
    public class Repository<T> : IRepository<T> where T : BaseDocument
    {
        protected readonly IMongoCollection<T> _collection;
        protected readonly UserContext _userContext;

        public async Task<List<T>> GetAllActiveAsync()
        {
            var filter = Builders<T>.Filter.Eq(x => x.IsDeleted, false);
            return await _collection.Find(filter).ToListAsync();
        }

        public Repository(IMongoDatabase database, UserContext userContext)
        {
            var collectionName = typeof(T).Name;// Tự động lấy tên class làm tên collection
            _collection = database.GetCollection<T>(collectionName);
            _userContext = userContext;
        }
        public async Task<IEnumerable<T>> GetAllAsync()
        {
            return await _collection.Find(d => !d.IsDeleted).ToListAsync();
        }
        public async Task<T> GetByIdAsync(Guid id)
        {
            return await _collection.Find(d => d.Id == id && !d.IsDeleted).FirstOrDefaultAsync();
        }
        public async Task<IEnumerable<T>> FilterAsync(Expression<Func<T, bool>> predicate)
        {
            var builder = Builders<T>.Filter;
            var combinedFilter = builder.And(
                builder.Eq(x => x.IsDeleted, false),
                builder.Where(predicate)
            );

            return await _collection.Find(combinedFilter).ToListAsync();
        }

        public async Task<T> FindOneAsync(Expression<Func<T, bool>> predicate) =>
            await _collection.Find(predicate).FirstOrDefaultAsync();
        
        public async Task<bool> AnyAsync(Expression<Func<T, bool>> predicate) =>
            await _collection.Find(predicate).AnyAsync();

        public async Task<bool> ExistsAsync(Expression<Func<T, bool>> predicate)
        {
            return await _collection.Find(predicate).AnyAsync();
        }

        public async Task<T> CreateAsync(T obj)
        {
            obj.CreatedBy = _userContext.UserId;
            await _collection.InsertOneAsync(obj);
            return obj;
        }
        public async Task UpdateAsync(T obj)
        {
            obj.UpdatedAt = DateTime.UtcNow;
            obj.UpdatedBy = _userContext.UserId;
            await _collection.ReplaceOneAsync(x => x.Id == obj.Id, obj);
        }
        public async Task DeleteAsync(Guid id)
        {
            var update = Builders<T>.Update
                .Set(d => d.IsDeleted, true)
                .Set(d => d.UpdatedAt, DateTime.UtcNow)
                .Set(d => d.UpdatedBy, _userContext.UserId);
            await _collection.UpdateOneAsync(x => x.Id == id, update);
        }
        public async Task<IEnumerable<object>> GetDistinctAsync(string fieldName)
        {
            return await _collection.Distinct<object>(fieldName, Builders<T>.Filter.Eq(d => d.IsDeleted, false)).ToListAsync();
        }
        public async Task<PaginatedList<T>> GetPagedListAsync(Expression<Func<T, bool>> predicate, int pageIndex, int pageSize)
        {
            var skip = pageIndex * pageSize; // skip dòng đầu tiên
            var filter = Builders<T>.Filter.Where(predicate) & Builders<T>.Filter.Eq(d => d.IsDeleted, false);
            var total = await _collection.CountDocumentsAsync(filter); // tổng số bản ghi
            var items = await _collection.Find(filter)
                                         .Skip(skip)
                                         .Limit(pageSize)
                                         .ToListAsync();
            return new PaginatedList<T>(items, (int)total, pageIndex, pageSize);
        }
        public async Task DeleteManyAsync(Expression<Func<T, bool>> predicate)
        {
            var filter = Builders<T>.Filter.Where(predicate) & Builders<T>.Filter.Eq(d => d.IsDeleted, false);
            var update = Builders<T>.Update
                .Set(d => d.IsDeleted, true)
                .Set(d => d.UpdatedAt, DateTime.UtcNow)
                .Set(d => d.UpdatedBy, _userContext.UserId);
            await _collection.UpdateManyAsync(filter, update);
        }

        public async Task InsertManyAsync(IEnumerable<T> documents)
        {
            var now = DateTime.UtcNow;
            var userId = _userContext.UserId;

            foreach (var doc in documents)
            {
                doc.Id = Guid.NewGuid();             
                doc.CreatedAt = now;                 
                doc.CreatedBy = userId;              
            }

            await _collection.InsertManyAsync(documents);
        }
    }
}
