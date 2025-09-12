package com.codecampus.organization.service.kafka;

import com.codecampus.constant.ScopeType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.org.OrganizationEvent;
import events.org.data.OrganizationPayload;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationEventProducer {
  KafkaTemplate<String, String> kafkaTemplate;
  ObjectMapper objectMapper;

  @Value("${app.event.organization-events:organization-events}")
  @NonFinal
  String ORG_EVENTS_TOPIC;

  public void publishCreated(String id, OrganizationPayload payload) {
    log.info("Sending ORG CREATED to topic: {}", ORG_EVENTS_TOPIC);
    publish(OrganizationEvent.Type.CREATED, id, payload);
  }

  public void publishUpdated(String id, OrganizationPayload payload) {
    publish(OrganizationEvent.Type.UPDATED, id, payload);
  }

  public void publishDeleted(String id) {
    publish(OrganizationEvent.Type.DELETED, id, null);
  }

  public void publishRestored(String id, OrganizationPayload payload) {
    publish(OrganizationEvent.Type.RESTORED, id, payload);
  }

  void publish(OrganizationEvent.Type type, String id,
               OrganizationPayload payload) {
    OrganizationEvent evt = OrganizationEvent.builder()
        .type(type)
        .id(id)
        .scopeType(ScopeType.Organization)
        .payload(payload)
        .build();

    try {
      String json = objectMapper.writeValueAsString(evt);
      kafkaTemplate.send(ORG_EVENTS_TOPIC, id, json);
    } catch (JsonProcessingException e) {
      log.error("[Kafka] Serialize thất bại", e);
      throw new RuntimeException(e);
    }
  }
}