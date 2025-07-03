package com.codecampus.submission.service;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.constant.submission.QuestionType;
import com.codecampus.submission.constant.submission.SubmissionStatus;
import com.codecampus.submission.dto.data.CodeJudgeResult;
import com.codecampus.submission.dto.request.AnswerDto;
import com.codecampus.submission.dto.request.CodeSubmissionRequest;
import com.codecampus.submission.dto.request.QuizSubmissionRequest;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Option;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.entity.SubmissionAnswer;
import com.codecampus.submission.entity.SubmissionResultDetail;
import com.codecampus.submission.entity.TestCase;
import com.codecampus.submission.entity.data.SubmissionAnswerId;
import com.codecampus.submission.entity.data.SubmissionResultId;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.helper.AuthenticationHelper;
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.repository.OptionRepository;
import com.codecampus.submission.repository.QuestionRepository;
import com.codecampus.submission.repository.SubmissionAnswerRepository;
import com.codecampus.submission.repository.SubmissionRepository;
import com.codecampus.submission.repository.SubmissionResultRepository;
import com.codecampus.submission.repository.TestCaseRepository;
import com.codecampus.submission.service.client.CodeJudgeClient;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionService
{
  ExerciseRepository exerciseRepository;
  SubmissionRepository submissionRepository;
  TestCaseRepository testCaseRepository;
  QuestionRepository questionRepository;
  OptionRepository optionRepository;
  SubmissionAnswerRepository submissionAnswerRepository;
  SubmissionResultRepository submissionResultRepository;
  CodeJudgeClient judge;

  @Transactional
  public String submitQuiz(QuizSubmissionRequest request)
  {
    Exercise exercise = exerciseRepository
        .findById(request.exerciseId())
        .orElseThrow(
            () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
        );
    if (exercise.getExerciseType() != ExerciseType.QUIZ)
    {
      throw new AppException(ErrorCode.EXERCISE_TYPE);
    }

    Submission submission = Submission.builder()
        .exercise(exercise)
        .userId(AuthenticationHelper.getMyUserId())
        .submittedAt(Instant.now())
        .status(SubmissionStatus.PENDING)
        .build();

    int total = 0;

    for (AnswerDto answerDto : request.answers())
    {
      Question question = questionRepository
          .findById(answerDto.questionId())
          .orElseThrow(
              () -> new AppException(ErrorCode.QUESTION_NOT_FOUND)
          );

      boolean correct;
      Option selectedOption = null;

      if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE)
      {
        selectedOption = optionRepository
            .findById(answerDto.selectedOptionId())
            .orElseThrow(
                () -> new AppException(ErrorCode.OPTION_NOT_FOUND)
            );
        correct = selectedOption.isCorrect();
      } else // fill-in-blank
      {
        correct = question
            .getOptions()
            .stream()
            .anyMatch(option -> option.isCorrect()
                && option.getOptionText().equalsIgnoreCase(
                answerDto.answerText()));
      }
      if (correct)
      {
        total += question.getPoints();
      }

      SubmissionAnswer submissionAnswer =
          SubmissionAnswer.builder()
              .id(new SubmissionAnswerId(null, question.getId()))
              .submission(submission)
              .question(question)
              .selectedOption(selectedOption)
              .answerText(answerDto.answerText())
              .correct(correct)
              .build();
      submissionAnswerRepository.save(submissionAnswer);
    }

    submission.setScore(total);
    submissionRepository.save(submission);

    return submission.getId();
  }

  @Transactional
  public String submitCode(CodeSubmissionRequest request)
  {
    Exercise exercise = exerciseRepository
        .findById(request.exerciseId())
        .orElseThrow(
            () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
        );
    if (exercise.getExerciseType() != ExerciseType.CODING)
    {
      throw new AppException(ErrorCode.EXERCISE_TYPE);
    }

    Submission submission = Submission.builder()
        .exercise(exercise)
        .userId(AuthenticationHelper.getMyUserId())
        .submittedAt(Instant.now())
        .language(request.language())
        .sourceCode(request.sourceCode())
        .status(SubmissionStatus.PENDING)
        .build();
    submissionRepository.save(submission);

    List<TestCase> testCases =
        exercise.getCodingDetail().getTestCases();
    int passed = 0;

    for (TestCase testCase : testCases)
    {
      CodeJudgeResult jr = judge.run(
          request.language(), request.sourceCode(),
          testCase.getInput(), testCase.getExpectedOutput(),
          exercise.getCodingDetail().getTimeLimit(),
          exercise.getCodingDetail().getMemoryLimit());

      SubmissionResultDetail resultDetail = SubmissionResultDetail.builder()
          .id(new SubmissionResultId(submission.getId(), testCase.getId()))
          .submission(submission)
          .testCase(testCase)
          .passed(jr.passed())
          .output(jr.output())
          .errorMessage(jr.error())
          .runTimeTs(jr.timeMs())
          .memoryUsed(jr.memoryKb())
          .build();

      if (jr.passed())
      {
        passed++;
      }
    }

    int score = (int) Math.round((double) passed / testCases.size() * 100);
    submission.setScore(score);
    submission.setStatus(
        passed == testCases.size() ? SubmissionStatus.ACCEPTED : SubmissionStatus.WRONG_ANSWER
    );

    // Ghi runtime/memory tối đa
    submission.setRuntime(
        submissionResultRepository
            .findBySubmission(submission)
            .stream()
            .mapToInt(SubmissionResultDetail::getRunTimeTs)
            .max()
            .orElse(0));
    submission.setMemoryUsed(
        submissionResultRepository
            .findBySubmission(submission)
            .stream()
            .mapToInt(SubmissionResultDetail::getMemoryUsed)
            .max()
            .orElse(0));

    return submission.getId();
  }
}
