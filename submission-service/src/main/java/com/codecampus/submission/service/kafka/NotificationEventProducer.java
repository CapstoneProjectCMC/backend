package com.codecampus.submission.service.kafka;

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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationEventProducer {

  KafkaTemplate<String, String> kafkaTemplate;
  ObjectMapper objectMapper;

  @Value("${app.event.notification-events}")
  @NonFinal
  String NOTIFICATION_EVENTS_TOPIC;

  public void publish(NotificationEvent event) {
    try {
      String json = objectMapper.writeValueAsString(event);
      // key = recipient để Kafka giữ ordering theo từng người nhận
      String key = event.getRecipient();
      kafkaTemplate.send(NOTIFICATION_EVENTS_TOPIC, key, json);
      log.info("[Kafka] Sent NotificationEvent to {} for {}",
          NOTIFICATION_EVENTS_TOPIC, key);
    } catch (JsonProcessingException e) {
      log.error("[Kafka] Serialize NotificationEvent failed", e);
      throw new RuntimeException(e);
    }
  }
}