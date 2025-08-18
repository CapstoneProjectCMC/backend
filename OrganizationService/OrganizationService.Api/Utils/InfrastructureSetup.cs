using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using OrganizationService.DataAccess.DbContexts;

namespace OrganizationService.Api.Utils
{
    public static class InfrastructureSetup
    {
        public static IHost MigrateDatabase(this IHost webHost)
        {
            using (var scope = webHost.Services.CreateScope())
            {
                var services = scope.ServiceProvider;
                var webHostEnvironment = services.GetRequiredService<IWebHostEnvironment>();
                using (var context = services.GetRequiredService<OrganizationServiceDbContext>())
                {
                    try
                    {
                      //  context.Database.Migrate();
                       // context.SeedData(webHostEnvironment);
                    }
                    catch
                    {
                        throw;
                    }
                }
            }

            return webHost;
        }

        public static void SeedData(this OrganizationServiceDbContext dbContext, IWebHostEnvironment webHostEnvironment)
        {
            if (dbContext.Users.Any())
            {
                return; // DB has been seeded
            }
           
            dbContext.SaveChanges();
        }
    }
}
