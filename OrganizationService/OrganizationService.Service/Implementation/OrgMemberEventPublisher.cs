using System.Text.Json;
using Confluent.Kafka;
using OrganizationService.Core.Constants;
using OrganizationService.Service.Interfaces;

namespace OrganizationService.Service.Implementation
{
    public class OrgMemberEventPublisher : IOrgMemberEventPublisher {
        private readonly IProducer<string,string> _producer;
        private readonly KafkaOptions _opt;
        public OrgMemberEventPublisher(IProducer<string,string> producer, KafkaOptions opt) {
            _producer=producer; _opt=opt;
        }
        public async Task PublishAsync(object evt) {
            var json = JsonSerializer.Serialize(evt);
            var root = JsonDocument.Parse(json).RootElement;
            var key = $"{root.GetProperty("scopeId").GetString()}:{root.GetProperty("userId").GetString()}";
            await _producer.ProduceAsync(_opt.Topics.OrgMemberEvents, new Message<string,string>{ Key = key, Value = json });
        }
    }
}