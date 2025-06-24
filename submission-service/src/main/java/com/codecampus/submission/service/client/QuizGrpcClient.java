package com.codecampus.submission.service.client;

import com.codecampus.quiz.grpc.ExerciseData;
import com.codecampus.quiz.grpc.OptionData;
import com.codecampus.quiz.grpc.QuestionData;
import com.codecampus.quiz.grpc.QuizServiceGrpc;
import com.codecampus.submission.entity.Exercise;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class QuizGrpcClient
{
  QuizServiceGrpc.QuizServiceBlockingStub quizStub;

  public void registerExercise(Exercise exercise)
  {
    ExerciseData.Builder exerciseData = ExerciseData.newBuilder()
        .setId(exercise.getId());

    exercise.getQuestions().forEach(q -> {
      QuestionData.Builder questionData = QuestionData.newBuilder()
          .setId(q.getId())
          .setText(q.getText())
          .setPoints(q.getPoints());

      q.getOptions().forEach(o -> questionData.addOptions(
          OptionData.newBuilder()
              .setId(o.getId())
              .setText(o.getText())
              .build()
      ));

      exerciseData.addQuestions(questionData);
    });

    quizStub.registerExercise(exerciseData.build()); // Empty Response
  }
}
