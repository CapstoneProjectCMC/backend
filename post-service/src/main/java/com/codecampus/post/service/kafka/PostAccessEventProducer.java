package com.codecampus.post.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import events.post.PostAccessEvent;
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
public class PostAccessEventProducer {

  KafkaTemplate<String, String> kafkaTemplate;
  ObjectMapper objectMapper;

  @Value("${app.event.post-access-events:post-access-events}")
  @NonFinal
  String TOPIC;

  public void publish(PostAccessEvent evt) {
    try {
      String json = objectMapper.writeValueAsString(evt);
      kafkaTemplate.send(TOPIC, evt.getPostId(), json);
    } catch (Exception e) {
      log.error("[Kafka] PostAccessEvent send failed", e);
      throw new RuntimeException(e);
    }
  }
}