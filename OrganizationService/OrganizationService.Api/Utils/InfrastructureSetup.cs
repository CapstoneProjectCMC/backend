using Microsoft.AspNetCore.Identity;

namespace OrganizationService.Api.Utils
{
    public static class InfrastructureSetup
    {
        //public static IHost MigrateDatabase(this IHost webHost)
        //{
        //    using (var scope = webHost.Services.CreateScope())
        //    {
        //        var services = scope.ServiceProvider;
        //        var webHostEnvironment = services.GetRequiredService<IWebHostEnvironment>();
        //        using (var context = services.GetRequiredService<OrganizationServiceDbContext>())
        //        {
        //            try
        //            {
        //                //context.Database.Migrate();
        //               // context.SeedData(webHostEnvironment);
        //            }
        //            catch
        //            {
        //                throw;
        //            }
        //        }
        //    }

        //    return webHost;
        //}

        //public static void SeedData(this OrganizationServiceDbContext dbContext, IWebHostEnvironment webHostEnvironment)
        //{
        //    if (dbContext.Users.Any())
        //    {
        //        return; // DB has been seeded
        //    }

        //    var userManager = dbContext.GetService<UserManager<User>>();

        //    var user = new User
        //    {
        //        Id = Guid.NewGuid(),
        //        UserName = "american_bankadmin",
        //        NameProfile = "Bank Admin User",
        //        NormalizedUserName = "ADMIN",
        //        Email = "admin@americanbank.com",
        //        NormalizedEmail = "ADMIN@AMERICANBANK.COM",
        //        EmailConfirmed = true,
        //        IsDeleted = false,
        //        IsLogin = false,
        //        CreatedAt = DateTime.UtcNow,
        //        CreatedBy = Guid.NewGuid(),
        //        SecurityStamp = Guid.NewGuid().ToString("D")
        //    };

        //    userManager.CreateAsync(user, "Amb2024@2024@2024").Wait();

        //    dbContext.SaveChanges();
        //}
    }
}
