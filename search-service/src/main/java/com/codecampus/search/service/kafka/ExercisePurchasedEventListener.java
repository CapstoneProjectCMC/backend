package com.codecampus.search.service.kafka;

import com.codecampus.search.repository.ExerciseDocumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.payment.ExercisePurchasedEvent;
import java.util.HashSet;
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

  ExerciseDocumentRepository exerciseRepo;
  ObjectMapper objectMapper;

  @KafkaListener(
      topics = "${app.event.exercise-purchased-events}",
      groupId = "search-service"
  )
  public void onExercisePurchased(String raw) {
    try {
      ExercisePurchasedEvent evt =
          objectMapper.readValue(raw, ExercisePurchasedEvent.class);
      String exerciseId = evt.getExerciseId();
      String userId = evt.getUserId();

      exerciseRepo.findById(exerciseId).ifPresent(doc -> {
        if (doc.getBuyerUserIds() == null) {
          doc.setBuyerUserIds(new HashSet<>());
        }
        doc.getBuyerUserIds().add(userId);
        exerciseRepo.save(doc);
      });
    } catch (Exception e) {
      log.error("[ExercisePurchasedEvent] handle failed: {}", e.getMessage(),
          e);
    }
  }
}