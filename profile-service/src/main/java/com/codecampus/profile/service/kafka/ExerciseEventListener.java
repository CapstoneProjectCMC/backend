package com.codecampus.profile.service.kafka;

import com.codecampus.profile.entity.Exercise;
import com.codecampus.profile.repository.ExerciseRepository;
import com.codecampus.profile.service.cache.ExerciseCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.exercise.ExerciseEvent;
import events.exercise.data.ExercisePayload;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseEventListener {
  ObjectMapper objectMapper;
  ExerciseRepository exerciseRepository;
  ExerciseCacheService exerciseCacheService;

  @KafkaListener(
      topics = "${app.event.exercise-events}",
      groupId = "profile-service"
  )
  @Transactional
  public void onExerciseEvent(String raw) {
    try {
      ExerciseEvent event = objectMapper.readValue(raw, ExerciseEvent.class);
      String exerciseId = event.getId();

      switch (event.getType()) {
        case CREATED, UPDATED -> {
          ExercisePayload payload = event.getPayload();
          if (payload == null) {
            break;
          }
          // Upsert node Exercise trong Neo4j
          Exercise node = exerciseRepository.findByExerciseId(exerciseId)
              .orElseGet(
                  () -> Exercise.builder().exerciseId(exerciseId).build());
          node.setTitle(payload.getTitle());
          node.setType(payload.getExerciseType()); // "QUIZ" | "CODING"
          node.setDifficulty(
              payload.getDifficulty() == null ? 0 : payload.getDifficulty());
          exerciseRepository.save(node);

          // Refresh cache để lần get tiếp theo có sẵn
          exerciseCacheService.refresh(exerciseId);
        }
        case DELETED -> {
          // Xoá cache
          exerciseCacheService.evictTwice(exerciseId);
          // Xoá node
          exerciseRepository.findByExerciseId(exerciseId)
              .ifPresent(exerciseRepository::delete);
        }
        default -> {
        }
      }
    } catch (Exception e) {
      log.error("[ExerciseEvent] parse/handle failed: {}", e.getMessage(), e);
    }
  }
}
