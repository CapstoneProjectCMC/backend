using System.Text.Json;
using Confluent.Kafka;
using OrganizationService.Core.Constants;
using OrganizationService.Service.Interfaces;

namespace OrganizationService.Service.Implementation
{
    public class OrgEventPublisher : IOrgEventPublisher {
        private readonly IProducer<string,string> _producer;
        private readonly KafkaOptions _opt;
        public OrgEventPublisher(IProducer<string,string> producer, KafkaOptions opt) {
            _producer = producer; _opt = opt;
        }
        public async Task PublishAsync(object evt) {
            var json = JsonSerializer.Serialize(evt);
            var key = JsonDocument.Parse(json).RootElement.GetProperty("id").GetString();
            await _producer.ProduceAsync(_opt.Topics.OrganizationEvents, new Message<string,string>{ Key = key, Value = json });
        }
    }
}