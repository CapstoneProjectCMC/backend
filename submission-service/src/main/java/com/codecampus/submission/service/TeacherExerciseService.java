package com.codecampus.submission.service;

import com.codecampus.quiz.grpc.QuizServiceGrpc;
import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.request.ExerciseCreationRequest;
import com.codecampus.submission.dto.response.exercise.ExerciseSummaryResponse;
import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Option;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.QuizDetail;
import com.codecampus.submission.entity.TestCase;
import com.codecampus.submission.mapper.ExerciseMapper;
import com.codecampus.submission.mapper.coding.CodingMapper;
import com.codecampus.submission.mapper.coding.TestCaseMapper;
import com.codecampus.submission.mapper.quiz.QuestionMapper;
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.repository.coding.CodingRepository;
import com.codecampus.submission.repository.coding.TestCaseRepository;
import com.codecampus.submission.repository.quiz.OptionRepository;
import com.codecampus.submission.repository.quiz.QuestionRepository;
import com.codecampus.submission.repository.quiz.QuizRepository;
import com.codecampus.submission.service.client.QuizGrpcClient;
import com.codecampus.submission.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeacherExerciseService
{

  QuizGrpcClient quizGrpcClient;

  ExerciseRepository exerciseRepository;
  CodingRepository codingRepository;
  TestCaseRepository testCaseRepository;
  QuizRepository quizRepository;
  QuestionRepository questionRepository;
  OptionRepository optionRepository;

  ExerciseMapper exerciseMapper;
  CodingMapper codingMapper;
  TestCaseMapper testCaseMapper;
  QuestionMapper questionMapper;

  QuizServiceGrpc.QuizServiceBlockingStub quizStub;

  @Transactional
  public ExerciseSummaryResponse createExercise(
      ExerciseCreationRequest request)
  {
    Exercise exercise = exerciseMapper.toExercise(request);
    exercise.setUserId(SecurityUtils.getMyUserId());
    exercise.setExerciseType(request.getExerciseType());
    exercise.setVisibility(true);

    // Nếu bài tập là CODING
    if (request.getExerciseType() == ExerciseType.CODING)
    {
      saveCoding(request, exercise);
    }

    // Nếu bài tập là QUIZ
    if (request.getExerciseType() == ExerciseType.QUIZ)
    {
      saveQuiz(request, exercise);
      quizGrpcClient.registerExercise(exercise);
    }

    return exerciseMapper
        .toExerciseSummaryResponse(exerciseRepository.save(exercise));
  }

  private void saveCoding(
      ExerciseCreationRequest request,
      Exercise exercise)
  {
    CodingDetail codingDetail = codingMapper.toCodingDetail(request.getCoding());
    codingDetail.setExercise(exercise);
    codingRepository.save(codingDetail);
    exercise.setCodingDetail(codingDetail);

    request.getCoding()
        .getTestCases()
        .stream()
        .map(testCaseData -> {
          TestCase testCase = testCaseMapper.toTestCase(testCaseData);
          testCase.setExercise(exercise);
          return testCase;
        })
        .forEach(testCaseRepository::save);
  }

  private void saveQuiz(
      ExerciseCreationRequest request,
      Exercise exercise)
  {
    QuizDetail quizDetail = QuizDetail.builder()
        .exercise(exercise)
        .numQuestions(request.getQuiz().getQuestions().size())
        .totalPoints(request.getQuiz().getTotalPoints())
        .build();
    quizRepository.save(quizDetail);
    exercise.setQuizDetail(quizDetail);

    request.getQuiz().getQuestions()
        .stream()
        .map(questionData -> {
          Question question = questionMapper.toQuestion(questionData);
          question.setExercise(exercise);
          return question;
        })
        .forEach(questionRepository::save);

    request.getQuiz().getQuestions().forEach(questionData -> {
      Question q = questionMapper.toQuestion(questionData);
      q.setExercise(exercise);
      questionRepository.save(q);

      questionData.getOptions().forEach(optionData -> {
        Option o = Option.builder()
            .id(optionData.getId())
            .question(q)
            .optionText(optionData.getText())
            .correct(optionData.isCorrect())
            .order(optionData.getOrder())
            .build();
        optionRepository.save(o);
        q.getOptions().add(o);
      });
    });
  }
}
