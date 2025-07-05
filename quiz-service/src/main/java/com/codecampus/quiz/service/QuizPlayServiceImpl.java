package com.codecampus.quiz.service;

import com.codecampus.quiz.entity.Option;
import com.codecampus.quiz.entity.Question;
import com.codecampus.quiz.entity.QuizExercise;
import com.codecampus.quiz.entity.QuizSubmission;
import com.codecampus.quiz.entity.QuizSubmissionAnswer;
import com.codecampus.quiz.entity.data.QuizSubmissionAnswerId;
import com.codecampus.quiz.exception.AppException;
import com.codecampus.quiz.exception.ErrorCode;
import com.codecampus.quiz.grpc.AnswerDto;
import com.codecampus.quiz.grpc.LoadQuizRequest;
import com.codecampus.quiz.grpc.LoadQuizResponse;
import com.codecampus.quiz.grpc.QuizPlayServiceGrpc;
import com.codecampus.quiz.grpc.SubmitQuizRequest;
import com.codecampus.quiz.grpc.SubmitQuizResponse;
import com.codecampus.quiz.mapper.QuizMapper;
import com.codecampus.quiz.repository.QuizExerciseRepository;
import com.codecampus.quiz.repository.QuizSubmissionRepository;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizPlayServiceImpl
    extends QuizPlayServiceGrpc.QuizPlayServiceImplBase
{
  QuizExerciseRepository quizExerciseRepository;
  QuizSubmissionRepository quizSubmissionRepository;
  QuizMapper quizMapper; // entity ↔ gRPC DTO

  /* ---------- Load quiz cho học sinh ---------- */
  @Override
  public void loadQuiz(
      LoadQuizRequest loadQuizRequest,
      StreamObserver<LoadQuizResponse> streamObserver)
  {
    QuizExercise quiz = quizExerciseRepository
        .findById(loadQuizRequest.getExerciseId())
        .orElseThrow(
            () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
        );

    // Ẩn cờ đúng/sai của Option trước khi map
    LoadQuizResponse res = quizMapper.toLoadQuizResponse(quiz);

    streamObserver.onNext(res);
    streamObserver.onCompleted();
  }

  /* ---------- Submit quiz ---------- */
  @Override
  @Transactional
  public void submitQuiz(
      SubmitQuizRequest submitQuizRequest,
      StreamObserver<SubmitQuizResponse> streamObserver)
  {
    QuizExercise quiz = quizExerciseRepository
        .findById(submitQuizRequest.getExerciseId())
        .orElseThrow(
            () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
        );

    int score = 0;
    QuizSubmission submission = new QuizSubmission();
    submission.setExerciseId(quiz.getId());
    submission.setStudentId(submitQuizRequest.getStudentId());
    submission.setSubmittedAt(Instant.now());
    submission.setTotalPoints(quiz.getTotalPoints());

    for (AnswerDto answerDto : submitQuizRequest.getAnswersList())
    {
      Question question = quiz
          .findQuestion(answerDto.getQuestionId())
          .orElseThrow(
              () -> new AppException(ErrorCode.QUESTION_NOT_FOUND)
          );

      boolean correct =
          switch (question.getQuestionType())
          {
            case SINGLE_CHOICE -> question.getOptions().stream()
                .filter(Option::isCorrect)
                .anyMatch(o -> o.getId().equals(answerDto.getSelectedOptionId()));
            case FILL_BLANK -> question.getOptions().isEmpty() &&
                question.getText().equalsIgnoreCase(answerDto.getAnswerText());
            case MULTI_CHOICE ->
            {
              var correctIds = question.getOptions().stream()
                  .filter(Option::isCorrect).map(Option::getId).toList();
              var chosen = Arrays.asList(answerDto.getSelectedOptionId().split(","));
              correct = new HashSet<>(correctIds).equals(new HashSet<>(chosen));
              yield correct;
            }
          };
      if (correct)
      {
        score += question.getPoints();
      }

      QuizSubmissionAnswer quizSubmissionAnswer = new QuizSubmissionAnswer(
          new QuizSubmissionAnswerId(submission.getId(), question.getId()),
          submission, question,
          question.optionById(answerDto.getSelectedOptionId()).orElse(null),
          answerDto.getAnswerText(), correct);
      submission.getAnswers().add(quizSubmissionAnswer);
    }
    submission.setScore(score);
    quizSubmissionRepository.save(submission);

    SubmitQuizResponse res = SubmitQuizResponse.newBuilder()
        .setScore(score)
        .setTotalPoints(quiz.getTotalPoints())
        .setPassed(score == quiz.getTotalPoints())
        .build();
    streamObserver.onNext(res);
    streamObserver.onCompleted();
  }
}
