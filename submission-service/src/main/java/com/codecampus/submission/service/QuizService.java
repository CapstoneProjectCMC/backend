package com.codecampus.submission.service;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.request.AddQuizDetailRequest;
import com.codecampus.submission.dto.request.QuestionDto;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.QuizDetail;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.mapper.QuestionMapper;
import com.codecampus.submission.repository.QuestionRepository;
import com.codecampus.submission.repository.QuizDetailRepository;
import com.codecampus.submission.service.client.GrpcQuizClient;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizService
{
  QuizDetailRepository quizDetailRepository;
  QuestionRepository questionRepository;
  GrpcQuizClient grpcQuizClient;

  QuestionMapper questionMapper;

  ExerciseService exerciseService;

  @Transactional
  public QuizDetail addQuizDetail(
      String exerciseId,
      AddQuizDetailRequest addQuizRequest)
  {
    Exercise exercise =
        exerciseService.getExerciseOrThrow(exerciseId);

    Assert.isTrue(
        exercise.getExerciseType() == ExerciseType.QUIZ,
        "Exercise không phải QUIZ"
    );
    if (exercise.getQuizDetail() != null)
    {
      throw new AppException(ErrorCode.EXERCISE_TYPE);
    }

    QuizDetail quizDetail = new QuizDetail();
    quizDetail.setExercise(exercise);

    int total = 0;
    for (QuestionDto questionDto : addQuizRequest.questions())
    {
      Question question =
          questionMapper.toQuestion(questionDto);
      question.setQuizDetail(quizDetail);
      quizDetail.getQuestions().add(question);
      total += question.getPoints();
    }
    quizDetail.setNumQuestions(quizDetail.getQuestions().size());
    quizDetail.setTotalPoints(total);
    quizDetailRepository.save(quizDetail);

    grpcQuizClient.pushQuizDetail(exerciseId, quizDetail);
    return quizDetail;
  }

  @Transactional
  public Question addQuestion(
      String exerciseId,
      QuestionDto questionDto) throws BadRequestException
  {

    Exercise exercise =
        exerciseService.getExerciseOrThrow(exerciseId);
    QuizDetail quizDetail = Optional
        .ofNullable(exercise.getQuizDetail())
        .orElseThrow(
            () -> new BadRequestException("Chưa có QuizDetail")
        );

    Question question =
        questionMapper.toQuestion(questionDto);
    question.setQuizDetail(quizDetail);

    quizDetail.getQuestions().add(question);
    quizDetail.setNumQuestions(quizDetail.getNumQuestions() + 1);
    quizDetail.setTotalPoints(quizDetail.getTotalPoints() + question.getPoints());
    questionRepository.save(question);

    grpcQuizClient.pushQuestion(exerciseId, question);
    return question;
  }
}
