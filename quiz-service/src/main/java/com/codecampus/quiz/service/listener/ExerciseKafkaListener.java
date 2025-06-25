package com.codecampus.quiz.service.listener;

import com.codecampus.quiz.grpc.ExerciseData;
import com.codecampus.quiz.grpc.GetExerciseRequest;
import com.codecampus.quiz.grpc.QuizServiceGrpc;
import com.codecampus.quiz.service.ExerciseService;
import event.ExerciseCreatedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseKafkaListener
{
  ExerciseService exerciseService;

  @NonFinal
  @GrpcClient("submissionGrpc")
  QuizServiceGrpc.QuizServiceBlockingStub submissionStub;

  @KafkaListener(
      topics = "${kafka.topic.exercise-created}",
      groupId = "quiz-service")
  public void listen(ExerciseCreatedEvent event)
  {
    if (!"QUIZ".equals(event.getType()))
    {
      return;
    }

    ExerciseData data = submissionStub.getExercise(
        GetExerciseRequest.newBuilder()
            .setExerciseId(event.getExerciseId())
            .build());

    exerciseService.saveQuizFromGrpc(data);

    log.info("Quiz saved from event {}", event.getExerciseId());
  }
}
