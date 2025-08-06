using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;
using Microsoft.Extensions.Configuration;
using System.IO;

namespace OrganizationService.DataAccess
{
    public class OrganizationServiceDbContextFactory : IDesignTimeDbContextFactory<DbContexts.OrganizationServiceDbContext>
    {
        public DbContexts.OrganizationServiceDbContext CreateDbContext(string[] args)
        {
            // Load cả appsettings.json và appsettings.Development.json
            var environmentName = Environment.GetEnvironmentVariable("ASPNETCORE_ENVIRONMENT") ?? "Development";

            IConfigurationRoot configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("appsettings.json", optional: false)
                .AddJsonFile($"appsettings.{environmentName}.json", optional: true) // load development
                .Build();

            var builder = new DbContextOptionsBuilder<DbContexts.OrganizationServiceDbContext>();
            var connectionString = configuration.GetConnectionString("Default");

            builder.UseSqlServer(connectionString);

            return new DbContexts.OrganizationServiceDbContext(builder.Options);
        }
    }
}
