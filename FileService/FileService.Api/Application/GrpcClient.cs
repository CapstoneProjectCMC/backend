using Grpc.Net.Client;
using SharedProtos.Protos;


namespace FileService.Api.Application
{
    public class GrpcClient
    {
        private readonly GrpcFile.GrpcFileClient _client;

        public GrpcClient()
        {
            var channel = GrpcChannel.ForAddress("http://localhost:50051");
            _client = new GrpcFile.GrpcFileClient(channel);
        }

        public async Task GetFilesAsync(string filter)
        {
            try
            {
                var request = new GetFilesRequest { Filter = filter };
                var response = await _client.GetFilesAsync(request);
                foreach (var file in response.Files)
                {
                    Console.WriteLine($"File: {file.FileId}, {file.FileName}, Org: {file.OrgId} - {file.OrgName}");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
            }
        }
    }
}
