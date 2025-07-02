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
                        FileName = "example.pdf",
                        FileType = "application/pdf",
                        Size = 123456,
                        Url = "https://example.com/files/example.pdf",
                        Checksum = "demo-checksum",
                        Category = "giáo trình",
                        Description = "Seed file để kiểm tra MongoDB",
                        ThumbnailUrl = "https://example.com/thumbs/example.png",
                        TranscodingStatus = "finished",
                        Tags = new() { "seed", "mongodb" },
                        IsLectureVideo = false,
                        IsTextbook = true,
                        OrgId = "demo-org",
                        ViewCount = 5,
                        Rating = 4.8,
                        AssociatedResourceIds = new() { "demo-resource" },
                        CreatedBy = Guid.NewGuid()
                    };

                    repo.CreateAsync(seed).Wait();
                    Console.WriteLine("Seeded 1 File document vào MongoDB.");
                }
                else
                {
                    Console.WriteLine("Dữ liệu đã tồn tại, không cần seed.");
                }
            }

            return webHost;
        }
    }
}
