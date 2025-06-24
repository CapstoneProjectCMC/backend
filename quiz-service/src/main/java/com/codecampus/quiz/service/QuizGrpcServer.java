package com.codecampus.quiz.service;

import com.codecampus.quiz.grpc.Answer;
import com.codecampus.quiz.grpc.ExerciseData;
import com.codecampus.quiz.grpc.GetExerciseRequest;
import com.codecampus.quiz.grpc.QuestionData;
import com.codecampus.quiz.grpc.QuizPayload;
import com.codecampus.quiz.grpc.QuizResult;
import com.codecampus.quiz.grpc.QuizServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizGrpcServer extends QuizServiceGrpc.QuizServiceImplBase
{
  ExerciseService exerciseService;

  /* Nhận bài tập từ submission-service */
  @Override
  public void registerExercise(
      ExerciseData request,
      StreamObserver<Empty> responseObserver)
  {
    exerciseService.saveQuizFromGrpc(request);
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }

  /* Học sinh lấy đề */
  @Override
  public void getExercise(
      GetExerciseRequest request,
      StreamObserver<ExerciseData> responseObserver)
  {
    ExerciseData data = exerciseService
        .toQuizProto(request.getExerciseId());
    responseObserver.onNext(data);
    responseObserver.onCompleted();
  }

  /* Chấm điểm & trả kết quả đơn giản */
  @Override
  public void submitQuiz(
      QuizPayload request,
      StreamObserver<QuizResult> responseObserver)
  {
    ExerciseData exerciseData = exerciseService
        .toQuizProto(request.getExerciseId());

    Map<String, String> correctMap = exerciseData
        .getQuestionsList()
        .stream()
        .collect(Collectors.toMap(
            QuestionData::getId,
            q -> q.getOptionsList().getFirst().getId())
        );

    int score = 0;
    for (Answer ans : request.getAnswersList())
    {
      if (Objects.equals(
          ans.getSelectedOptionId(),
          correctMap.get(ans.getQuestionId())
      ))
      {
        int pts = exerciseData.getQuestionsList().stream()
            .filter(q -> q.getId().equals(ans.getQuestionId()))
            .findFirst()
            .map(QuestionData::getPoints)
            .orElse(0);
        score += pts;
      }
    }

  }
}
