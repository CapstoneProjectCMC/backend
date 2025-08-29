package com.codecampus.organization.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import events.org.OrganizationMemberEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrganizationMemberEventProducer {
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Value("${app.event.organization-member-events:organization-member-events}")
  String ORG_MEMBER_EVENTS_TOPIC;

  public void publish(OrganizationMemberEvent evt) {
    try {
      String payload = objectMapper.writeValueAsString(evt);
      kafkaTemplate.send(ORG_MEMBER_EVENTS_TOPIC, evt.getUserId(), payload);
    } catch (Exception e) {
      log.error("[Kafka] Serialize thất bại", e);
      throw new RuntimeException(e);
    }
  }
}