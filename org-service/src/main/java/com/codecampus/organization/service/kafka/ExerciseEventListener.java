package com.codecampus.organization.service.kafka;

import com.codecampus.organization.service.ExerciseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.exercise.ExerciseEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseEventListener {

  ObjectMapper objectMapper;
  ExerciseService orgExerciseService;

  @KafkaListener(
      topics = "${app.event.exercise-events:exercise-events}",
      groupId = "organization-service"
  )
  public void onExerciseEvent(String raw) {
    try {
      ExerciseEvent evt = objectMapper.readValue(raw, ExerciseEvent.class);
      switch (evt.getType()) {
        case CREATED, UPDATED -> orgExerciseService
            .addOrUpdateFromExercisePayload(evt.getId(), evt.getPayload());
        case DELETED -> orgExerciseService.softDeleteByExerciseId(evt.getId());
        default -> { /* no-op */ }
      }
    } catch (Exception e) {
      log.error("[ExerciseEventListener] Parse/handle failed: {}",
          e.getMessage(), e);
    }
  }
}