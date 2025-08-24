package com.codecampus.submission.service.kafka;

import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.mapper.ExercisePayloadMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.exercise.ExerciseEvent;
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
public class ExerciseEventProducer {

  KafkaTemplate<String, String> kafkaTemplate;
  ObjectMapper objectMapper;
  ExercisePayloadMapper exercisePayloadMapper;
  @Value("${app.event.exercise-events}")
  @NonFinal
  String EXERCISE_EVENTS_TOPIC;

  public void publishCreatedExerciseEvent(
      Exercise exercise) {
    log.info("Sending to Kafka topic: {}", EXERCISE_EVENTS_TOPIC);
    publishEvent(ExerciseEvent.Type.CREATED, exercise);
  }

  public void publishUpdatedExerciseEvent(Exercise exercise) {
    publishEvent(ExerciseEvent.Type.UPDATED, exercise);
  }

  public void publishDeletedExerciseEvent(Exercise exercise) {
    publishEvent(ExerciseEvent.Type.DELETED, exercise);
  }

  void publishEvent(
      ExerciseEvent.Type type,
      Exercise exercise) {
    ExerciseEvent exerciseEvent = ExerciseEvent.builder()
        .type(type)
        .id(exercise.getId())
        .payload(type == ExerciseEvent.Type.DELETED ? null
            :
            exercisePayloadMapper.toExercisePayloadFromExercise(
                exercise))
        .build();

    try {
      String jsonObject = objectMapper.writeValueAsString(exerciseEvent);

      kafkaTemplate.send(
          EXERCISE_EVENTS_TOPIC,
          exercise.getId(),
          jsonObject
      );
    } catch (JsonProcessingException exception) {
      log.error("[Kafka] Serialize thất bại", exception);
      throw new RuntimeException(exception);
    }

  }
}
