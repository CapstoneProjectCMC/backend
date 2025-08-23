namespace OrganizationService.Service.Interfaces
{
    public interface IOrgMemberEventPublisher {
        Task PublishAsync(object evt);
    }
}

