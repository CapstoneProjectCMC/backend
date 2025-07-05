package com.codecampus.quiz.service;

import com.codecampus.quiz.entity.Question;
import com.codecampus.quiz.entity.QuizExercise;
import com.codecampus.quiz.exception.AppException;
import com.codecampus.quiz.exception.ErrorCode;
import com.codecampus.quiz.grpc.AddQuestionRequest;
import com.codecampus.quiz.grpc.AddQuizDetailRequest;
import com.codecampus.quiz.grpc.CreateQuizExerciseRequest;
import com.codecampus.quiz.grpc.QuizSyncServiceGrpc;
import com.codecampus.quiz.mapper.QuizMapper;
import com.codecampus.quiz.repository.QuestionRepository;
import com.codecampus.quiz.repository.QuizExerciseRepository;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizSyncServiceImpl
    extends QuizSyncServiceGrpc.QuizSyncServiceImplBase
{
  QuizExerciseRepository quizExerciseRepository;
  QuestionRepository questionRepository;
  QuizMapper quizMapper;  // gRPC DTO → entity

  @Override
  public void createQuizExercise(
      CreateQuizExerciseRequest createQuizRequest,
      StreamObserver<Empty> streamObserver)
  {
    if (quizExerciseRepository
        .existsById(createQuizRequest.getExercise().getId()))
    {
      throw new AppException(ErrorCode.EXERCISE_DUPLICATED);
    }

    QuizExercise exercise = quizMapper.toEntity(
        createQuizRequest.getExercise());
    quizExerciseRepository.save(exercise);

    streamObserver.onNext(Empty.getDefaultInstance());
    streamObserver.onCompleted();
  }

  @Override
  @Transactional
  public void addQuizDetail(
      AddQuizDetailRequest addQuizRequest,
      StreamObserver<Empty> streamObserver)
  {
    QuizExercise quiz = quizExerciseRepository
        .findById(addQuizRequest.getExerciseId())
        .orElseThrow(
            () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
        );

    addQuizRequest.getQuestionsList()
        .forEach(questionDto -> {
          Question question =
              quizMapper.toEntity(questionDto);
          question.setQuiz(quiz);
          quiz.getQuestions().add(question);
        });
    recalc(quiz);
    quizExerciseRepository.save(quiz);

    streamObserver.onNext(Empty.getDefaultInstance());
    streamObserver.onCompleted();
  }

  @Override
  @Transactional
  public void addQuestion(
      AddQuestionRequest addQuestionRequest,
      StreamObserver<Empty> streamObserver)
  {
    QuizExercise quiz = quizExerciseRepository
        .findById(addQuestionRequest.getExerciseId())
        .orElseThrow(
            () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
        );

    Question question = quizMapper.toEntity(addQuestionRequest.getQuestion());
    question.setQuiz(quiz);
    recalc(quiz);
    questionRepository.save(question);

    streamObserver.onNext(Empty.getDefaultInstance());
    streamObserver.onCompleted();
  }

  private void recalc(QuizExercise quiz)
  {
    quiz.setNumQuestions(quiz.getQuestions().size());
    quiz.setTotalPoints(
        quiz.getQuestions().stream()
            .mapToInt(Question::getPoints).sum());
  }
}

