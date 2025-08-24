namespace OrganizationService.Core.Constants;

public record KafkaOptions
{
    public string BootstrapServers { get; set; } = "";
    public TopicsOptions Topics { get; set; } = new();

    public record TopicsOptions
    {
        public string OrganizationEvents { get; set; } = "organization-events";
        public string OrgMemberEvents { get; set; } = "org-member-events";
    }
}