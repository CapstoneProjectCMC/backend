package com.codecampus.payment.service.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.payment.ExercisePurchasedEvent;
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
public class ExercisePurchasedEventProducer {

  KafkaTemplate<String, String> kafkaTemplate;
  ObjectMapper objectMapper;

  @Value("${app.event.exercise-purchased-events}")
  @NonFinal
  String EXERCISE_PURCHASED_TOPIC;

  public void publish(ExercisePurchasedEvent event) {
    try {
      String json = objectMapper.writeValueAsString(event);
      // Key = exerciseId:userId để đảm bảo ordering idempotent theo cặp
      String key = event.getExerciseId() + ":" + event.getUserId();
      kafkaTemplate.send(EXERCISE_PURCHASED_TOPIC, key, json);
      log.info("[Kafka] Sent ExercisePurchasedEvent to {} key={}",
          EXERCISE_PURCHASED_TOPIC, key);
    } catch (JsonProcessingException e) {
      log.error("[Kafka] Serialize ExercisePurchasedEvent failed", e);
      throw new RuntimeException(e);
    }
  }
}