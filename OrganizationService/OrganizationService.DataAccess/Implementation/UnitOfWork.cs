using OrganizationService.DataAccess.DbContexts;
using OrganizationService.DataAccess.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security;
using System.Text;
using System.Threading.Tasks;

namespace OrganizationService.DataAccess.Implementation
{
    public class UnitOfWork : IUnitOfWork
    {
        public UnitOfWork(OrganizationServiceDbContext dbContext)
        {
            _dbContext = dbContext;
        }

        private readonly OrganizationServiceDbContext _dbContext;
        public async Task SaveChangesAsync()
        {
            await _dbContext.SaveChangesAsync();
        }
    }
}
