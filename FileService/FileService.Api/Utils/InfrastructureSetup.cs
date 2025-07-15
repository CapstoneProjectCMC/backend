using FileService.Core.Enums;
using FileService.DataAccess.Interfaces;
using FileService.DataAccess.Models;

namespace FileService.Api.Utils
{
    public static class InfrastructureSetup
    {
        public static IHost MigrateMongoDb(this IHost webHost)
        {
            using (var scope = webHost.Services.CreateScope())
            {
                var services = scope.ServiceProvider;

                var repo = services.GetRequiredService<IRepository<FileDocument>>();

                // Kiểm tra nếu chưa có file nào thì seed thử
                var hasFile = repo.AnyAsync(f => true).Result;
                if (!hasFile)
                {
                    var seed = new FileDocument
                    {
                        FileName = "20250413_134739.mp4",
                        FileType = "video/mp4",
                        Size = 22316829,
                        Url = "/static/39509bcb-83ce-4794-bf76-ab46dc69bdd3.mp4",
                        Checksum = "check-sum-demo",
                        Category = FileCategory.Video,
                        Description = "Seed file để kiểm tra MongoDB",
                        ThumbnailUrl = "/thumbnails/e02ea408-c424-46a1-8936-06a51bd69bab.jpg",
                        TranscodingStatus = "success",
                        Tags = new() { "#seed", "#mongodb" },
                        IsLectureVideo = true,
                        IsTextbook = false,
                        OrgId = null,
                        ViewCount = 0,
                        Rating = 0,
                        AssociatedResourceIds = new() { "demo-resource" },
                        CreatedBy = Guid.NewGuid(),
                        CreatedAt = DateTime.UtcNow
                    };

                    repo.CreateAsync(seed).Wait();
                    Console.WriteLine("Seeded 1 File document vào MongoDB.");
                }
                else
                {
                    Console.WriteLine("Data existed. Need not seed");
                }
            }

            return webHost;
        }
    }
}



