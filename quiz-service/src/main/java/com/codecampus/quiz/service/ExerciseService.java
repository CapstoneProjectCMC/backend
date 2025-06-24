package com.codecampus.quiz.service;

import com.codecampus.quiz.entity.QuizExercise;
import com.codecampus.quiz.entity.QuizOption;
import com.codecampus.quiz.entity.QuizQuestion;
import com.codecampus.quiz.exception.AppException;
import com.codecampus.quiz.exception.ErrorCode;
import com.codecampus.quiz.grpc.ExerciseData;
import com.codecampus.quiz.grpc.OptionData;
import com.codecampus.quiz.grpc.QuestionData;
import com.codecampus.quiz.repository.QuizExerciseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseService
{
  QuizExerciseRepository quizExerciseRepository;

  @Transactional
  public void saveQuizFromGrpc(ExerciseData exerciseData)
  {
    QuizExercise exercise = QuizExercise.builder()
        .id(exerciseData.getId())
        .build();

    exerciseData.getQuestionsList().forEach(q -> {
      QuizQuestion quizQuestion = QuizQuestion.builder()
          .id(q.getId())
          .exercise(exercise)
          .text(q.getText())
          .points(q.getPoints())
          .build();

      q.getOptionsList().forEach(o ->
          quizQuestion.getOptions().add(
              QuizOption.builder()
                  .id(o.getId())
                  .question(quizQuestion)
                  .text(o.getText())
                  .build()
          ));

      exercise.getQuestions().add(quizQuestion);
    });
  }

  @Transactional(readOnly = true)
  public ExerciseData toQuizProto(String exerciseId)
  {
    QuizExercise exercise = quizExerciseRepository
        .findById(exerciseId)
        .orElseThrow(
            () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
        );

    ExerciseData.Builder exerciseDataBuilder = ExerciseData
        .newBuilder()
        .setId(exercise.getId());

    exercise.getQuestions().forEach(q -> {
      QuestionData.Builder questionBuilder = QuestionData.newBuilder()
          .setId(q.getId())
          .setText(q.getText())
          .setPoints(q.getPoints());

      q.getOptions().forEach(o ->
          questionBuilder.addOptions(
              OptionData.newBuilder()
                  .setId(o.getId())
                  .setText(o.getText())
                  .build()
          ));

      exerciseDataBuilder.addQuestions(questionBuilder.build());
    });

    return exerciseDataBuilder.build();
  }
}
