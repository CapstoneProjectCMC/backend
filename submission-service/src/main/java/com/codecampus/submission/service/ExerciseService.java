package com.codecampus.submission.service;

import com.codecampus.submission.constant.sort.SortField;
import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.constant.submission.SubmissionStatus;
import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.data.ExerciseSummaryDto;
import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.dto.request.UpdateExerciseRequest;
import com.codecampus.submission.dto.request.coding.CreateCodingExerciseRequest;
import com.codecampus.submission.dto.request.quiz.CreateQuizExerciseRequest;
import com.codecampus.submission.dto.response.ExerciseResponse;
import com.codecampus.submission.dto.response.coding.coding_detail.CodingDetailSliceDetailResponse;
import com.codecampus.submission.dto.response.coding.coding_detail.ExerciseCodingDetailResponse;
import com.codecampus.submission.dto.response.quiz.quiz_detail.ExerciseQuizDetailResponse;
import com.codecampus.submission.dto.response.quiz.quiz_detail.QuizDetailSliceDetailResponse;
import com.codecampus.submission.entity.Assignment;
import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.QuizDetail;
import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.entity.SubmissionResultDetail;
import com.codecampus.submission.entity.TestCase;
import com.codecampus.submission.entity.data.SubmissionResultId;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.grpc.CodeSubmissionDto;
import com.codecampus.submission.grpc.CreateCodeSubmissionRequest;
import com.codecampus.submission.grpc.CreateQuizSubmissionRequest;
import com.codecampus.submission.grpc.QuizSubmissionDto;
import com.codecampus.submission.helper.AuthenticationHelper;
import com.codecampus.submission.helper.CodingHelper;
import com.codecampus.submission.helper.ExerciseHelper;
import com.codecampus.submission.helper.PageResponseHelper;
import com.codecampus.submission.helper.QuizHelper;
import com.codecampus.submission.helper.SortHelper;
import com.codecampus.submission.mapper.ExerciseMapper;
import com.codecampus.submission.mapper.SubmissionMapper;
import com.codecampus.submission.repository.AssignmentRepository;
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.repository.QuestionRepository;
import com.codecampus.submission.repository.SubmissionRepository;
import com.codecampus.submission.repository.SubmissionResultRepository;
import com.codecampus.submission.repository.TestCaseRepository;
import com.codecampus.submission.repository.client.PaymentClient;
import com.codecampus.submission.service.cache.UserBulkLoader;
import com.codecampus.submission.service.cache.UserSummaryCacheService;
import com.codecampus.submission.service.grpc.GrpcCodingClient;
import com.codecampus.submission.service.grpc.GrpcQuizClient;
import com.codecampus.submission.service.kafka.ExerciseEventProducer;
import com.codecampus.submission.service.kafka.ExerciseStatusEventProducer;
import com.codecampus.submission.service.kafka.NotificationEventProducer;
import dtos.ExerciseStatusDto;
import dtos.UserSummary;
import events.notification.NotificationEvent;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseService {
  ExerciseRepository exerciseRepository;
  QuestionRepository questionRepository;
  SubmissionRepository submissionRepository;
  AssignmentRepository assignmentRepository;
  TestCaseRepository testCaseRepository;
  SubmissionResultRepository submissionResultRepository;
  UserBulkLoader userBulkLoader;

  UserSummaryCacheService userSummaryCacheService;
  ContestService contestService;
  AssignmentService assignmentService;
  QuizService quizService;
  CodingService codingService;

  GrpcQuizClient grpcQuizClient;
  GrpcCodingClient grpcCodingClient;
  PaymentClient paymentClient;
  ExerciseEventProducer exerciseEventProducer;
  ExerciseStatusEventProducer exerciseStatusEventProducer;
  NotificationEventProducer notificationEventProducer;

  ExerciseMapper exerciseMapper;
  SubmissionMapper submissionMapper;

  QuizHelper quizHelper;
  CodingHelper codingHelper;
  ExerciseHelper exerciseHelper;


  @Transactional
  public Exercise createExercise(
      CreateExerciseRequest request,
      boolean returnExercise) {
    String userId = AuthenticationHelper.getMyUserId();

    Exercise exercise = exerciseRepository
        .save(exerciseMapper.toExerciseFromCreateExerciseRequest(
            request, userId));
    if (exercise.getExerciseType() == ExerciseType.QUIZ) {
      grpcQuizClient.pushExercise(exercise);
    } else if (exercise.getExerciseType() == ExerciseType.CODING) {
      grpcCodingClient.pushExercise(exercise);
    }

    exerciseEventProducer.publishCreatedExerciseEvent(exercise);

    ExerciseStatusDto exerciseStatusDto = new ExerciseStatusDto(
        exercise.getId(),
        userId, // chính giáo viên / creator
        /* created   */ true,
        /* completed */ false,
        /* completedAt */ null,
        /* attempts   */ null,
        /* bestScore  */ null,
        /* totalPts   */ null);
    exerciseStatusEventProducer.publishUpsert(exerciseStatusDto);

    if (returnExercise) {
      return exercise;
    }
    return null;
  }

  @Transactional
  public Exercise createQuizExercise(
      CreateQuizExerciseRequest request,
      boolean returnExercise) {

    Exercise exercise =
        createExercise(
            request.createExerciseRequest(),
            true
        );
    exerciseRepository.saveAndFlush(exercise);

    quizService.addQuizDetail(
        exercise.getId(),
        request.addQuizDetailRequest(),
        false);

    if (returnExercise) {
      return exercise;
    }
    return null;
  }

  /**
   * Tạo CODING exercise + coding detail trong 1 call
   */
  @Transactional
  public Exercise createCodingExercise(
      CreateCodingExerciseRequest request,
      boolean returnExercise) {
    Exercise exercise = createExercise(
        request.createExerciseRequest(), true);
    exerciseRepository.saveAndFlush(exercise);

    // đẩy coding detail
    codingService.addCodingDetail(
        exercise.getId(),
        request.addCodingDetailRequest(),
        false);

    if (returnExercise) {
      return exercise;
    }
    return null;
  }


  @Transactional
  public void createQuizSubmission(
      CreateQuizSubmissionRequest request) {
    QuizSubmissionDto quizSubmissionDto = request.getSubmission();

    Exercise exercise = exerciseHelper
        .getExerciseOrThrow(quizSubmissionDto.getExerciseId());

    Submission submission = submissionMapper
        .toSubmissionFromQuizSubmissionDto(
            quizSubmissionDto,
            exercise,
            questionRepository
        );
    submissionRepository.save(submission);

    // Set Complete
    assignmentRepository
        .findByExerciseIdAndStudentId(
            quizSubmissionDto.getExerciseId(),
            quizSubmissionDto.getStudentId())
        .ifPresent(a -> a.setCompleted(true));

    assignmentService.markCompleted(
        quizSubmissionDto.getExerciseId(),
        quizSubmissionDto.getStudentId()
    );

    NotificationEvent evt = NotificationEvent.builder()
        .channel("SOCKET")
        .recipient(submission.getUserId())
        .templateCode("SUBMISSION_PASSED")
        .subject("Bạn đã nộp bài")
        .body("Bạn vừa nộp: " + exercise.getTitle())
        .param(Map.of(
            "exerciseId", exercise.getId(),
            "submissionId", submission.getId(),
            "score", submission.getScore()
        ))
        .build();
    notificationEventProducer.publish(evt);

    // Cập nhật xếp hạng contest
    contestService.updateRankingOnSubmission(submission);
  }

  @Transactional
  public void createCodeSubmission(
      CreateCodeSubmissionRequest request) {

    CodeSubmissionDto codeSubmissionDto = request.getSubmission();

    Exercise exercise =
        exerciseHelper.getExerciseOrThrow(
            codeSubmissionDto.getExerciseId());

    Submission submission = Submission.builder()
        .exercise(exercise)
        .userId(codeSubmissionDto.getStudentId())
        .submittedAt(Instant.ofEpochSecond(
            codeSubmissionDto.getSubmittedAt().getSeconds(),
            codeSubmissionDto.getSubmittedAt().getNanos()))
        .language(codeSubmissionDto.getLanguage())
        .sourceCode(codeSubmissionDto.getSourceCode())
        .timeTakenSeconds(codeSubmissionDto.getTimeTakenSeconds())
        .score(codeSubmissionDto.getScore())
        .memoryUsed(codeSubmissionDto.getPeakMemoryMb())
        .status(codeSubmissionDto.getScore() ==
            codeSubmissionDto.getTotalPoints()
            ? SubmissionStatus.PASSED
            : (codeSubmissionDto.getScore() == 0
            ? SubmissionStatus.FAILED
            : SubmissionStatus.PARTIAL))
        .build();
    submissionRepository.save(submission);

    // Lưu chi tiết Testcase
    codeSubmissionDto.getResultsList().forEach(r -> {
      TestCase testCase = testCaseRepository
          .findById(r.getTestCaseId())
          .orElseThrow(() -> new AppException(
              ErrorCode.TESTCASE_NOT_FOUND));

      submissionResultRepository.save(SubmissionResultDetail.builder()
          .id(new SubmissionResultId(submission.getId(),
              testCase.getId()))
          .submission(submission)
          .testCase(testCase)
          .passed(r.getPassed())
          .runTimeTs(r.getRuntimeMs())
          .memoryUsed(r.getMemoryMb())
          .output(r.getOutput())
          .errorMessage(r.getErrorMessage())
          .build());
    });

    /* Side-effects */
    assignmentService.markCompleted(
        exercise.getId(),
        submission.getUserId());

    NotificationEvent evt = NotificationEvent.builder()
        .channel("SOCKET")
        .recipient(submission.getUserId())
        .templateCode("SUBMISSION_PASSED")
        .subject("Bạn đã nộp bài")
        .body("Bạn vừa nộp: " + exercise.getTitle())
        .param(Map.of(
            "exerciseId", exercise.getId(),
            "submissionId", submission.getId(),
            "score", submission.getScore()
        ))
        .build();
    notificationEventProducer.publish(evt);

    contestService.updateRankingOnSubmission(submission);
  }

  @Transactional
  public void updateExercise(
      String id, UpdateExerciseRequest request) {
    Exercise exercise = exerciseHelper
        .getExerciseOrThrow(id);
    exerciseMapper.patchUpdateExerciseRequestToExercise(request, exercise);

    if (exercise.getExerciseType() == ExerciseType.QUIZ) {
      grpcQuizClient.pushExercise(exercise);
    } else if (exercise.getExerciseType() == ExerciseType.CODING) {
      grpcCodingClient.pushExercise(exercise);
    }
    exerciseEventProducer.publishUpdatedExerciseEvent(exercise);
  }

  @Transactional
  public void softDeleteExercise(String exerciseId) {
    Exercise exercise = exerciseHelper
        .getExerciseOrThrow(exerciseId);
    String userId = AuthenticationHelper.getMyUserId();
    String by = AuthenticationHelper.getMyUsername();
    exerciseHelper.markExerciseDeletedRecursively(exercise, by);
    exerciseRepository.save(exercise);

    ExerciseStatusDto exerciseStatusDto =
        new ExerciseStatusDto(
            exerciseId, userId,
            /* created   */ null,
            /* completed */ null,
            /* completedAt */ null,
            /* attempts   */ null,
            /* bestScore  */ null,
            /* totalPts   */ null);

    exerciseStatusEventProducer.publishDeleted(exerciseStatusDto);

    exerciseEventProducer.publishDeletedExerciseEvent(exercise);
    if (exercise.getExerciseType() ==
        ExerciseType.QUIZ) {
      grpcQuizClient.softDeleteExercise(exerciseId);
    } else if (exercise.getExerciseType()
        == ExerciseType.CODING) {
      grpcCodingClient.softDeleteExercise(exerciseId);
    }
  }

  public PageResponse<ExerciseResponse> getAllExercises(
      int page, int size,
      SortField sortBy, boolean asc) {

    Pageable pageable = PageRequest.of(
        page - 1,
        size,
        SortHelper.build(sortBy, asc));

    Page<Exercise> pageData = exerciseRepository
        .findAll(pageable);
    // .map(exerciseMapper::toExerciseQuizResponseFromExercise);

    // Bulk load user summaries
    Set<String> userIds = pageData
        .stream()
        .map(Exercise::getUserId)
        .collect(Collectors.toSet());
    Map<String, UserSummary> summaries = userBulkLoader.loadAll(userIds);

    String me = AuthenticationHelper.getMyUserId();
    Map<String, Boolean> purchasedMap = (me == null)
        ? java.util.Collections.emptyMap()
        : purchasedBulk(me, pageData.stream().map(Exercise::getId).toList());

    Page<ExerciseResponse> out = pageData
        .map(
            e -> exerciseHelper.toExerciseResponseFromExerciseAndUserSummary(
                e,
                summaries.get(e.getUserId()),
                Boolean.TRUE.equals(purchasedMap.get(e.getId()))
            ))
        .map(a -> a); // no-op để giữ Page map

    return PageResponseHelper.toPageResponse(out, page);
  }

  public PageResponse<ExerciseResponse> getExercisesOf(
      int page, int size,
      SortField sortBy, boolean asc) {

    Pageable pageable = PageRequest.of(
        page - 1,
        size,
        SortHelper.build(sortBy, asc));

    Page<Exercise> pageData = exerciseRepository
        .findByUserId(AuthenticationHelper.getMyUserId(), pageable);
    // .map(exerciseMapper::toExerciseQuizResponseFromExercise);

    Set<String> userIds = pageData.stream().map(Exercise::getUserId)
        .collect(java.util.stream.Collectors.toSet());
    Map<String, UserSummary> summaries = userBulkLoader.loadAll(userIds);

    String me = AuthenticationHelper.getMyUserId();
    Map<String, Boolean> purchasedMap = (me == null)
        ? java.util.Collections.emptyMap()
        : purchasedBulk(me, pageData.stream().map(Exercise::getId).toList());

    Page<ExerciseResponse> out = pageData
        .map(
            e -> exerciseHelper.toExerciseResponseFromExerciseAndUserSummary(
                e,
                summaries.get(e.getUserId()),
                Boolean.TRUE.equals(purchasedMap.get(e.getId()))
            ));

    return PageResponseHelper.toPageResponse(out, page);
  }

  @Transactional(readOnly = true)
  public ExerciseQuizDetailResponse getQuizExerciseDetail(
      String exerciseId,
      int qPage, int qSize,
      SortField qSortBy, boolean qAsc) {

    Exercise exercise =
        exerciseHelper.getExerciseOrThrow(exerciseId);

    if (exercise.getExerciseType() != ExerciseType.QUIZ) {
      throw new AppException(ErrorCode.EXERCISE_TYPE);
    }

    QuizDetail quizDetail = Optional.ofNullable(
            exercise.getQuizDetail())
        .orElseThrow(() -> new AppException(
            ErrorCode.QUIZ_DETAIL_NOT_FOUND));

    QuizDetailSliceDetailResponse qSlice =
        quizHelper.buildQuizSliceWithOptions(
            quizDetail, qPage, qSize, qSortBy, qAsc
        );

    UserSummary userSummary =
        userSummaryCacheService.getOrLoad(exercise.getUserId());

    String me = AuthenticationHelper.getMyUserId();
    boolean purchased = (me != null) && purchasedBy(me, exerciseId);

    return exerciseHelper.toExerciseQuizDetailResponseFromExerciseQuizDetailSliceDetailResponseAndUserSummary(
        exercise, qSlice, userSummary, purchased);
  }

  @Transactional(readOnly = true)
  public ExerciseCodingDetailResponse getCodingExerciseDetail(
      String exerciseId,
      int tcPage, int tcSize,
      SortField tcSortBy, boolean tcAsc) {

    Exercise exercise =
        exerciseHelper.getExerciseOrThrow(exerciseId);

    if (exercise.getExerciseType() != ExerciseType.CODING) {
      throw new AppException(ErrorCode.EXERCISE_TYPE);
    }

    CodingDetail codingDetail = Optional.ofNullable(
            exercise.getCodingDetail())
        .orElseThrow(() -> new AppException(
            ErrorCode.CODING_DETAIL_NOT_FOUND));

    CodingDetailSliceDetailResponse slice = codingHelper.buildCodingSlice(
        codingDetail, tcPage, tcSize, tcSortBy, tcAsc);

    UserSummary userSummary =
        userSummaryCacheService.getOrLoad(exercise.getUserId());

    String me = AuthenticationHelper.getMyUserId();
    boolean purchased = (me != null) && purchasedBy(me, exerciseId);

    return exerciseHelper.toExerciseCodingDetailResponseFromExerciseCodingDetailSliceDetailResponseAndUserSummary(
        exercise, slice, userSummary, purchased);
  }

  @Transactional(readOnly = true)
  public ExerciseSummaryDto getExerciseSummary(
      String exerciseId) {
    Exercise exercise = exerciseHelper.getExerciseOrThrow(exerciseId);
    String me = AuthenticationHelper.getMyUserId();

    boolean created = me != null && me.equals(exercise.getUserId());
    boolean completed = assignmentRepository
        .findByExerciseIdAndStudentId(exerciseId, me)
        .map(Assignment::isCompleted)
        .orElse(false);

    boolean purchased = (me != null) && purchasedBy(me, exerciseId);

    return exerciseMapper.toExerciseSyncDtoFromExercise(
        exercise, created, completed, purchased);
  }

  @Transactional(readOnly = true)
  public ExerciseStatusDto getExerciseStatus(
      String exerciseId,
      String studentId) {

    Exercise exercise = exerciseRepository
        .findById(exerciseId)
        .orElse(null);

    boolean created =
        exercise != null && studentId != null &&
            studentId.equals(exercise.getUserId());

    Optional<Assignment> assignment = assignmentRepository
        .findByExerciseIdAndStudentId(exerciseId,
            studentId);

    boolean completed =
        assignment
            .map(Assignment::isCompleted)
            .orElse(false);

    return new ExerciseStatusDto(
        exerciseId,
        studentId,
        created,
        completed,
        /* completedAt */ null,
        // có thể tra từ Submission đầu PASSED nếu muốn
        /* attempts   */ null,
        /* bestScore  */ null,
        /* totalPts   */ null
    );
  }


  private boolean purchasedBy(
      String userId, String exerciseId) {
    try {
      var api =
          paymentClient.internalHasPurchased(userId, exerciseId, "EXERCISE");
      return api != null && Boolean.TRUE.equals(api.getResult());
    } catch (Exception ex) {
      log.warn("[Exercise] check purchased error userId={}, exerciseId={}: {}",
          userId, exerciseId, ex.getMessage());
      return false; // fallback an toàn
    }
  }

  private Map<String, Boolean> purchasedBulk(String userId,
                                             java.util.List<String> exerciseIds) {
    try {
      var api =
          paymentClient.internalHasPurchasedBulk(userId, "EXERCISE",
              exerciseIds);
      return api != null && api.getResult() != null ? api.getResult() :
          java.util.Collections.emptyMap();
    } catch (Exception ex) {
      log.warn(
          "[Exercise] bulk check purchased error userId={}, ids={}, err={}",
          userId, exerciseIds.size(), ex.getMessage());
      return java.util.Collections.emptyMap();
    }
  }

}
