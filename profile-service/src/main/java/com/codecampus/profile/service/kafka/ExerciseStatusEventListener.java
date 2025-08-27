package com.codecampus.profile.service.kafka;

import com.codecampus.profile.entity.Exercise;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.entity.properties.exercise.CompletedExercise;
import com.codecampus.profile.entity.properties.exercise.CreatedExercise;
import com.codecampus.profile.repository.ExerciseRepository;
import com.codecampus.profile.repository.UserProfileRepository;
import com.codecampus.profile.service.cache.ExerciseStatusCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import dtos.ExerciseStatusDto;
import events.exercise.ExerciseStatusEvent;
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
public class ExerciseStatusEventListener {

  ObjectMapper objectMapper;
  UserProfileRepository userProfileRepository;
  ExerciseRepository exerciseRepository;
  ExerciseStatusCacheService statusCache;

  @KafkaListener(
      topics = "${app.event.exercise-status-events}",
      groupId = "profile-service")
  @Transactional
  public void onExerciseStatus(String raw) {
    try {
      ExerciseStatusEvent evt =
          objectMapper.readValue(raw, ExerciseStatusEvent.class);
      if (evt.getPayload() == null) {
        return;
      }
      upsert(evt.getPayload());
    } catch (Exception e) {
      log.error("[ExerciseStatusEvent] handle failed: {}", raw, e);
    }
  }

  @Transactional
  void upsert(ExerciseStatusDto p) {
    // 1. User
    var userOpt = userProfileRepository.findActiveByUserId(p.studentId());
    if (userOpt.isEmpty()) {
      log.warn("User {} not found, skip status", p.studentId());
      return;
    }
    UserProfile user = userOpt.get();

    // 2. Exercise node
    Exercise exercise = exerciseRepository.mergeByExerciseId(p.exerciseId());

    /* 3. Quan hệ CREATED_EXERCISE */
    if (Boolean.TRUE.equals(p.created())) {
      boolean exists = user.getCreatedExercises().stream()
          .anyMatch(ce -> ce.getExercise() != null
              && p.exerciseId().equals(ce.getExercise().getExerciseId()));
      if (!exists) {
        user.getCreatedExercises().add(
            CreatedExercise.builder().exercise(exercise).build());
      }
    }

    /* 4. Quan hệ COMPLETED_EXERCISE */
    user.getCompletedExercises()
        .removeIf(ce -> ce.getExercise() != null
            && p.exerciseId().equals(
            ce.getExercise().getExerciseId()));

    if (Boolean.TRUE.equals(p.completed())) {
      user.getCompletedExercises().add(
          CompletedExercise.builder()
              .exercise(exercise)
              .attempts(p.attempts() == null ? 0 : p.attempts())
              .score(p.bestScore() == null ? 0 : p.bestScore())
              .completedAt(p.completedAt())
              .build());
    }

    userProfileRepository.save(user);
    statusCache.refresh(p.studentId(), p.exerciseId());
  }
}