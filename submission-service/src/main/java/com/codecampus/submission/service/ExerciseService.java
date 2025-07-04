package com.codecampus.submission.service;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.request.AddCodingDetailRequest;
import com.codecampus.submission.dto.request.AddQuizDetailRequest;
import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.dto.request.QuestionDto;
import com.codecampus.submission.dto.request.TestCaseDto;
import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Option;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.QuizDetail;
import com.codecampus.submission.entity.TestCase;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.helper.AuthenticationHelper;
import com.codecampus.submission.mapper.ExerciseMapper;
import com.codecampus.submission.repository.CodingDetailRepository;
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.repository.OptionRepository;
import com.codecampus.submission.repository.QuestionRepository;
import com.codecampus.submission.repository.QuizDetailRepository;
import com.codecampus.submission.repository.TestCaseRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseService
{
  ExerciseRepository exerciseRepository;
  TestCaseRepository testCaseRepository;
  CodingDetailRepository codingDetailRepository;
  QuestionRepository questionRepository;
  OptionRepository optionRepository;

  ExerciseMapper exerciseMapper;
  private final QuizDetailRepository quizDetailRepository;

  @Transactional
  public String createExercise(
      CreateExerciseRequest request)
  {
    Exercise exercise = Exercise.builder()
        .userId(AuthenticationHelper.getMyUserId())
        .title(request.tittle())
        .description(request.description())
        .difficulty(request.difficulty())
        .exerciseType(request.exerciseType())
        .orgId(request.orgId())
        .cost(request.cost())
        .freeForOrg(request.freeForOrg())
        .startTime(request.startTime())
        .endTime(request.endTime())
        .duration(request.duration())
        .allowDiscussionId(request.allowDiscussionId())
        .resourceIds(request.resourceIds())
        .tags(request.tags())
        .allowAiQuestion(request.allowAiQuestion())
        .build();

    return exerciseRepository.save(exercise).getId();
  }

  @Transactional
  public void addCodingDetail(
      String exerciseId,
      AddCodingDetailRequest request)
  {
    Exercise exercise = exerciseRepository
        .findById(exerciseId).orElseThrow(
            () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
        );
    if (exercise.getExerciseType() != ExerciseType.CODING)
    {
      throw new AppException(ErrorCode.EXERCISE_TYPE);
    }
    if (exercise.getCodingDetail() != null)
    {
      throw new AppException(ErrorCode.CODING_ALREADY_EXISTS);
    }

    CodingDetail codingDetail =
        exerciseMapper.toCodingDetail(request);
    codingDetail.setExercise(exercise);

    List<TestCase> testCases = exerciseMapper
        .toTestCases(request.testCases());
    testCases.forEach(tc -> {
      tc.setCodingDetail(codingDetail);
      testCaseRepository.save(tc);
    });

    codingDetail.setTestCases(testCases);
    codingDetailRepository.save(codingDetail);

    exercise.setCodingDetail(codingDetail);
    exerciseRepository.save(exercise);
  }

  @Transactional
  public void addQuizDetail(
      String exerciseId,
      AddQuizDetailRequest request)
  {
    Exercise exercise = exerciseRepository
        .findById(exerciseId).orElseThrow(
            () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
        );
    if (exercise.getExerciseType() != ExerciseType.QUIZ)
    {
      throw new AppException(ErrorCode.EXERCISE_TYPE);
    }
    if (exercise.getQuizDetail() != null)
    {
      throw new AppException(ErrorCode.QUIZ_ALREADY_EXISTS);
    }

    QuizDetail quizDetail = new QuizDetail();
    quizDetail.setExercise(exercise);

    List<Question> questions = request
        .questions()
        .stream()
        .map(questionDto -> {
          Question q = Question.builder()
              .quizDetail(quizDetail)
              .text(questionDto.text())
              .questionType(questionDto.questionType())
              .points(questionDto.points())
              .orderInQuiz(questionDto.orderInQuiz())
              .build();

          questionRepository.save(q);

          List<Option> options = questionDto
              .options()
              .stream()
              .map(optionDto -> {
                Option o = Option.builder()
                    .question(q)
                    .optionText(optionDto.optionText())
                    .correct(optionDto.correct())
                    .order(optionDto.order())
                    .build();

                optionRepository.save(o);

                return o;
              }).toList();
          q.setOptions(options);
          return q;
        }).toList();

    quizDetail.setQuestions(questions);
    quizDetail.setNumQuestions(questions.size());
    quizDetail.setTotalPoints(questions
        .stream()
        .mapToInt(Question::getPoints)
        .sum()
    );
    quizDetailRepository.save(quizDetail);

    exercise.setQuizDetail(quizDetail);
    exerciseRepository.save(exercise);
  }

  @Transactional
  public void addTestCase(
      String exerciseId,
      TestCaseDto dto)
  {
    Exercise exercise = exerciseRepository
        .findById(exerciseId)
        .orElseThrow(
            () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
        );
    CodingDetail codingDetail = Optional
        .ofNullable(exercise.getCodingDetail())
        .orElseThrow(
            () -> new AppException(ErrorCode.CODING_NOT_FOUND)
        );

    TestCase testCase =
        exerciseMapper.toTestCases(List.of(dto)).getFirst();

    testCase.setCodingDetail(codingDetail);
    testCaseRepository.save(testCase);

    codingDetail.getTestCases().add(testCase);
    codingDetailRepository.save(codingDetail);
  }

  @Transactional
  public void addQuestion(
      String exerciseId,
      QuestionDto dto)
  {
    Exercise exercise = exerciseRepository
        .findById(exerciseId)
        .orElseThrow(
            () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
        );

    QuizDetail quizDetail = Optional
        .ofNullable(exercise.getQuizDetail())
        .orElseThrow(
            () -> new AppException(ErrorCode.QUIZ_NOT_FOUND)
        );

    Question question =
        exerciseMapper.toQuestions(List.of(dto)).getFirst();
    question.setQuizDetail(quizDetail);
    questionRepository.save(question);

    quizDetail.getQuestions().add(question);
    quizDetail.setNumQuestions(quizDetail.getNumQuestions() + 1);
    quizDetail.setTotalPoints(quizDetail.getTotalPoints() + question.getPoints());
    quizDetailRepository.save(quizDetail);
  }
}
