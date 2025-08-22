package com.codecampus.search.service.kafka;

import com.codecampus.search.mapper.ExerciseMapper;
import com.codecampus.search.repository.ExerciseDocumentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    ExerciseDocumentRepository exerciseDocumentRepository;

    ExerciseMapper exerciseMapper;
    ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${app.event.exercise-events}",
            groupId = "search-service")
    public void onMessageExercise(String raw) {
        try {
            ExerciseEvent exerciseEvent =
                    objectMapper.readValue(raw, ExerciseEvent.class);
            switch (exerciseEvent.getType()) {
                case CREATED, UPDATED -> exerciseDocumentRepository.save(
                        exerciseMapper.toExerciseDocumentFromExercisePayload(
                                exerciseEvent.getPayload()));
                case DELETED -> exerciseDocumentRepository.deleteById(
                        exerciseEvent.getId());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
