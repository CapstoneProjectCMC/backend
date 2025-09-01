package com.codecampus.identity.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.notification.NotificationEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationEventProducer {

  KafkaTemplate<String, String> kafkaTemplate;
  ObjectMapper objectMapper;

  @Value("${app.event.notification-events}")
  @NonFinal
  String NOTIFICATION_EVENTS_TOPIC;

  public void publish(NotificationEvent evt) {
    try {
      String json = objectMapper.writeValueAsString(evt);
      kafkaTemplate.send(NOTIFICATION_EVENTS_TOPIC, evt.getRecipient(), json);
    } catch (JsonProcessingException e) {
      log.error("[Kafka] Serialize notification failed", e);
      throw new RuntimeException(e);
    }
  }
}