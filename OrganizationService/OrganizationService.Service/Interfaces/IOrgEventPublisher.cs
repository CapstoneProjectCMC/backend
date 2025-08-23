namespace OrganizationService.Service.Interfaces
{
    public interface IOrgEventPublisher {
        Task PublishAsync(object evt);
    }
}