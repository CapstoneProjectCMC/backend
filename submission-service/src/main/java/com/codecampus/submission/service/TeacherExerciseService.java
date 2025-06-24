package com.codecampus.submission.service;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.request.ExerciseCreationRequest;
import com.codecampus.submission.dto.response.exercise.ExerciseSummaryResponse;
import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.Exercise;
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

  @Transactional
  public ExerciseSummaryResponse createExercise(
      ExerciseCreationRequest request)
  {
    Exercise exercise = exerciseMapper.toExercise(request);
    exercise = Exercise.builder()
        .userId(SecurityUtils.getMyUserId())
        .exerciseType(request.getExerciseType())
        .visibility(true)
        .build();

    // Nếu bài tập là CODING
    if (request.getExerciseType() == ExerciseType.CODING)
    {
      CodingDetail codingDetail = codingMapper.toCodingDetail(request.getCoding());
      codingDetail.setExercise(exercise);
      codingRepository.save(codingDetail);
      exercise.setCodingDetail(codingDetail);

      Exercise finalExercise = exercise;
      request.getCoding()
          .getTestCases()
          .stream()
          .map(testCaseData -> {
            TestCase testCase = testCaseMapper.toTestCase(testCaseData);
            testCase.setExercise(finalExercise);
            return testCase;
          })
          .forEach(testCaseRepository::save);
    }

    // Nếu bài tập là QUIZ
    if (request.getExerciseType() == ExerciseType.QUIZ)
    {
      QuizDetail quizDetail = QuizDetail.builder()
          .exercise(exercise)
          .numQuestions(request.getQuiz().getQuestions().size())
          .totalPoints(request.getQuiz().getTotalPoints())
          .build();
      quizRepository.save(quizDetail);
      exercise.setQuizDetail(quizDetail);

      Exercise finalExercise1 = exercise;
      request.getQuiz().getQuestions()
          .stream()
          .map(questionData -> {
            Question question = questionMapper.toQuestion(questionData);
            question.setExercise(finalExercise1);
            return question;
          })
          .forEach(questionRepository::save);
    }

    return exerciseMapper
        .toExerciseSummaryResponse(exerciseRepository.save(exercise));
  }
}
