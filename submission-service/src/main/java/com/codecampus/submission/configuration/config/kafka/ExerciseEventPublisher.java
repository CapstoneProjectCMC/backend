package com.codecampus.submission.configuration.config.kafka;

import com.codecampus.submission.entity.Exercise;
import event.ExerciseCreatedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseEventPublisher
{
  KafkaTemplate<String, ExerciseCreatedEvent> kafkaTemplate;

  @NonFinal
  @Value("${kafka.topic.exercise-created}")
  String topic;

  public void publishCreated(Exercise exercise)
  {
    ExerciseCreatedEvent event = new ExerciseCreatedEvent(
        exercise.getId(),
        exercise.getExerciseType().name()
    );

    kafkaTemplate.send(topic, exercise.getId(), event);
  }
}
