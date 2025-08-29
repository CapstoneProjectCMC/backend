package com.codecampus.organization.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import events.org.OrganizationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrganizationEventProducer {
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Value("${app.event.organization-events:organization-events}")
  String ORG_EVENTS_TOPIC;

  public void publish(OrganizationEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send(ORG_EVENTS_TOPIC, event.getId(), payload);
    } catch (Exception e) {
      log.error("[Kafka] Serialize thất bại", e);
      throw new RuntimeException(e);
    }
  }
}