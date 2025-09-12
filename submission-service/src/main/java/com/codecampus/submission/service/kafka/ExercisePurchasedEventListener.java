package com.codecampus.submission.service.kafka;

import com.codecampus.submission.service.AssignmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.payment.ExercisePurchasedEvent;
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
public class ExercisePurchasedEventListener {
  ObjectMapper objectMapper;
  AssignmentService assignmentService;

  @KafkaListener(
      topics = "${app.event.exercise-purchased-events}",
      groupId = "submission-service"
  )
  public void onExercisePurchased(String raw) {
    try {
      ExercisePurchasedEvent evt =
          objectMapper.readValue(raw, ExercisePurchasedEvent.class);

      // Mở khoá = upsert assignment cho user–exercise (dueAt = null)
      assignmentService.assignExercise(
          evt.getExerciseId(),
          evt.getUserId(),
          null
      );

      log.info("[ExercisePurchasedEvent] Unlocked exercise={} for user={}",
          evt.getExerciseId(), evt.getUserId());

    } catch (Exception e) {
      // Không ném exception để tránh Kafka re-delivery vô tận nếu lỗi “hard”
      log.error("[ExercisePurchasedEvent] handle failed: {}", e.getMessage(),
          e);
    }
  }
}
