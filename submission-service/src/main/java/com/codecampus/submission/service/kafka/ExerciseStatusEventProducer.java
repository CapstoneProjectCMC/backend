package com.codecampus.submission.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dtos.ExerciseStatusDto;
import events.exercise.ExerciseStatusEvent;
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
public class ExerciseStatusEventProducer {

  KafkaTemplate<String, String> kafkaTemplate;
  ObjectMapper objectMapper;

  @Value("${app.event.exercise-status-events}")
  @NonFinal
  String EXERCISE_STATUS_TOPIC;

  public void publishUpsert(ExerciseStatusDto exerciseStatusDto) {
    publish(ExerciseStatusEvent.Type.UPSERT, exerciseStatusDto);
  }

  public void publishDeleted(ExerciseStatusDto exerciseStatusDto) {
    publish(ExerciseStatusEvent.Type.DELETED, exerciseStatusDto);
  }

  public void publish(
      ExerciseStatusEvent.Type type,
      ExerciseStatusDto exerciseStatusDto) {

    ExerciseStatusEvent event = ExerciseStatusEvent.builder()
        .id(exerciseStatusDto.exerciseId())
        .type(type)
        .payload(exerciseStatusDto)
        .build();

    try {
      String json = objectMapper.writeValueAsString(event);
      // key để bảo đảm ordering theo 1 HS – 1 BT
      String key = event.getPayload().exerciseId() + ":" +
          event.getPayload().studentId();

      kafkaTemplate.send(
          EXERCISE_STATUS_TOPIC,
          key,
          json);
    } catch (JsonProcessingException e) {
      log.error("[Kafka] Serialize ExerciseStatusEvent fail", e);
      throw new RuntimeException(e);
    }
  }

}
