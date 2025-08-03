using Microsoft.EntityFrameworkCore.ChangeTracking;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using OrganizationService.Core.Enums;
using OrganizationService.Core.Exceptions;

namespace OrganizationService.DataAccess.DbContexts
{
    public class OrganizationServiceDbContext : IdentityDbContext
    {
        private readonly OrganizationService.Core.ApiModels.UserContext _userContext;
        private readonly IConfiguration _configuration;
        private readonly IServiceProvider _serviceProvider;
        public OrganizationServiceDbContext(DbContextOptions<OrganizationServiceDbContext> options, OrganizationService.Core.ApiModels.UserContext userContext, IConfiguration configuration, IServiceProvider serviceProvider) : base(options)
        {
            _userContext = userContext;
            _configuration = configuration;
            _serviceProvider = serviceProvider;
        }

        protected override void OnModelCreating(ModelBuilder builder)
        {
            base.OnModelCreating(builder);
        }

        public override async Task<int> SaveChangesAsync(CancellationToken cancellationToken = default)
        {
            try
            {
                int rowCount;
                rowCount = await base.SaveChangesAsync(cancellationToken);

                await base.SaveChangesAsync();
                return rowCount;
            }
            catch (DbUpdateConcurrencyException ex)
            {
                throw new ErrorException(StatusCodeEnum.ConcurrencyConflict);
            }
        }
    }
}

