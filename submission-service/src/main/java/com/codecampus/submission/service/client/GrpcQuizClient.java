package com.codecampus.submission.service.client;

import com.codecampus.quiz.grpc.AddQuestionRequest;
import com.codecampus.quiz.grpc.AddQuizDetailRequest;
import com.codecampus.quiz.grpc.CreateQuizExerciseRequest;
import com.codecampus.quiz.grpc.QuizSyncServiceGrpc;
import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.QuizDetail;
import com.codecampus.submission.mapper.QuestionMapper;
import com.codecampus.submission.mapper.QuizMapper;
import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GrpcQuizClient
{
  QuizSyncServiceGrpc.QuizSyncServiceBlockingStub stub;
  QuestionMapper questionMapper;
  QuizMapper quizMapper;

  public void pushExercise(Exercise exercise)
  {
    if (exercise.getExerciseType() != ExerciseType.QUIZ)
    {
      return;
    }
    try
    {
      CreateQuizExerciseRequest createRequest = CreateQuizExerciseRequest.newBuilder()
          .setExercise(quizMapper.toGrpc(exercise))
          .build();
      stub.createQuizExercise(createRequest);
    } catch (StatusRuntimeException ex)
    {
      log.error("[gRPC] pushExercise lỗi: {}", ex.getStatus(), ex);
      throw ex;
    }

  }

  public void pushQuizDetail(
      String exerciseId,
      QuizDetail quizDetail)
  {
    AddQuizDetailRequest addQuizRequest =
        AddQuizDetailRequest.newBuilder()
            .setExerciseId(exerciseId)
            .addAllQuestions(
                quizDetail.getQuestions()
                    .stream()
                    .map(questionMapper::toGrpc)
                    .toList())
            .build();
    stub.addQuizDetail(addQuizRequest);
  }

  public void pushQuestion(
      String exerciseId,
      Question question)
  {
    AddQuestionRequest addQuestionRequest =
        AddQuestionRequest.newBuilder()
            .setExerciseId(exerciseId)
            .setQuestion(questionMapper.toGrpc(question))
            .build();
    stub.addQuestion(addQuestionRequest);
  }
}
