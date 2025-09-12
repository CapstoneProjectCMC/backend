package com.codecampus.submission.service;


import com.codecampus.submission.constant.sort.SortField;
import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.response.stats.ExerciseStatRow;
import com.codecampus.submission.dto.response.stats.SummaryStat;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.helper.AuthenticationHelper;
import com.codecampus.submission.helper.PageResponseHelper;
import com.codecampus.submission.helper.SortHelper;
import com.codecampus.submission.repository.AssignmentRepository;
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.repository.SubmissionRepository;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsService {

  ExerciseRepository exerciseRepository;
  AssignmentRepository assignmentRepository;
  SubmissionRepository submissionRepository;

  /* -------------------- TEACHER: danh sách bài tập kèm thống kê -------------------- */
  public PageResponse<ExerciseStatRow> getTeacherExerciseStats(
      int page, int size,
      SortField sortBy, boolean asc) {

    String teacherId = AuthenticationHelper.getMyUserId();

    Pageable pageable = PageRequest.of(
        Math.max(page - 1, 0),
        Math.max(size, 1),
        SortHelper.build(sortBy, asc)
    );

    Page<Exercise> exercisePage =
        exerciseRepository.findByUserId(teacherId, pageable);

    List<String> ids =
        exercisePage.getContent().stream().map(Exercise::getId).toList();
    Map<String, Long> assigned =
        toLongMap(assignmentRepository.countAssignmentsByExerciseIds(ids));
    Map<String, Long> completed = toLongMap(
        assignmentRepository.countCompletedAssignmentsByExerciseIds(ids));
    Map<String, Long> subs =
        toLongMap(submissionRepository.countSubmissionsByExerciseIds(ids));
    Map<String, Long> passed = toLongMap(
        submissionRepository.countPassedSubmissionsByExerciseIds(ids));
    Map<String, Double> avgScore =
        toDoubleMap(submissionRepository.avgScoreByExerciseIds(ids));
    Map<String, Instant> lastAt =
        toInstantMap(submissionRepository.lastSubmissionAtByExerciseIds(ids));

    Page<ExerciseStatRow> mapped = exercisePage.map(e -> {
      long a = assigned.getOrDefault(e.getId(), 0L);
      long c = completed.getOrDefault(e.getId(), 0L);
      long sub = subs.getOrDefault(e.getId(), 0L);
      long pas = passed.getOrDefault(e.getId(), 0L);
      Double compRate = a > 0 ? (c * 1.0 / a) : null;
      Double passRate = sub > 0 ? (pas * 1.0 / sub) : null;
      return ExerciseStatRow.builder()
          .exerciseId(e.getId())
          .title(e.getTitle())
          .exerciseType(e.getExerciseType())
          .visibility(e.isVisibility())
          .orgId(e.getOrgId())
          .assignedCount(a)
          .completedCount(c)
          .completionRate(compRate)
          .submissionCount(sub)
          .passedCount(pas)
          .passRate(passRate)
          .avgScore(avgScore.get(e.getId()))
          .lastSubmissionAt(lastAt.get(e.getId()))
          .build();
    });

    return PageResponseHelper.toPageResponse(mapped, page);
  }

  /* -------------------- ADMIN: danh sách bài tập kèm thống kê -------------------- */
  public PageResponse<ExerciseStatRow> getAdminExerciseStats(
      int page, int size,
      SortField sortBy, boolean asc) {

    Pageable pageable = PageRequest.of(
        Math.max(page - 1, 0),
        Math.max(size, 1),
        SortHelper.build(sortBy, asc)
    );

    Page<Exercise> exercisePage = exerciseRepository.findAll(pageable);

    List<String> ids =
        exercisePage.getContent().stream().map(Exercise::getId).toList();
    Map<String, Long> assigned =
        toLongMap(assignmentRepository.countAssignmentsByExerciseIds(ids));
    Map<String, Long> completed = toLongMap(
        assignmentRepository.countCompletedAssignmentsByExerciseIds(ids));
    Map<String, Long> subs =
        toLongMap(submissionRepository.countSubmissionsByExerciseIds(ids));
    Map<String, Long> passed = toLongMap(
        submissionRepository.countPassedSubmissionsByExerciseIds(ids));
    Map<String, Double> avgScore =
        toDoubleMap(submissionRepository.avgScoreByExerciseIds(ids));
    Map<String, Instant> lastAt =
        toInstantMap(submissionRepository.lastSubmissionAtByExerciseIds(ids));

    Page<ExerciseStatRow> mapped = exercisePage.map(e -> {
      long a = assigned.getOrDefault(e.getId(), 0L);
      long c = completed.getOrDefault(e.getId(), 0L);
      long sub = subs.getOrDefault(e.getId(), 0L);
      long pas = passed.getOrDefault(e.getId(), 0L);
      Double compRate = a > 0 ? (c * 1.0 / a) : null;
      Double passRate = sub > 0 ? (pas * 1.0 / sub) : null;
      return ExerciseStatRow.builder()
          .exerciseId(e.getId())
          .title(e.getTitle())
          .exerciseType(e.getExerciseType())
          .visibility(e.isVisibility())
          .orgId(e.getOrgId())
          .assignedCount(a)
          .completedCount(c)
          .completionRate(compRate)
          .submissionCount(sub)
          .passedCount(pas)
          .passRate(passRate)
          .avgScore(avgScore.get(e.getId()))
          .lastSubmissionAt(lastAt.get(e.getId()))
          .build();
    });

    return PageResponseHelper.toPageResponse(mapped, page);
  }

  /* -------------------- ADMIN: số liệu tổng quan (dashboard) -------------------- */
  public SummaryStat getAdminSummary() {
    long totalExercises = exerciseRepository.count();
    long totalVisible = exerciseRepository.findAll()
        .stream().filter(Exercise::isVisibility).count();
    long totalQuiz = exerciseRepository.findAll()
        .stream().filter(e -> e.getExerciseType().name().equals("QUIZ"))
        .count();
    long totalCoding = totalExercises - totalQuiz;

    long totalAssignments = assignmentRepository.count();
    long totalCompleted = assignmentRepository
        .findAll().stream().filter(a -> a.isCompleted()).count();

    long totalSubs = submissionRepository.countAllSubmissions();
    long totalPassed = submissionRepository.countAllPassedSubmissions();

    return SummaryStat.builder()
        .totalExercises(totalExercises)
        .totalVisibleExercises(totalVisible)
        .totalQuiz(totalQuiz)
        .totalCoding(totalCoding)
        .totalAssignments(totalAssignments)
        .totalCompletedAssignments(totalCompleted)
        .totalSubmissions(totalSubs)
        .totalPassedSubmissions(totalPassed)
        .build();
  }

  Map<String, Long> toLongMap(List<Object[]> rows) {
    Map<String, Long> m = new HashMap<>();
    for (Object[] r : rows) {
      m.put((String) r[0], ((Number) r[1]).longValue());
    }
    return m;
  }

  Map<String, Double> toDoubleMap(List<Object[]> rows) {
    Map<String, Double> m = new HashMap<>();
    for (Object[] r : rows) {
      Number n = (Number) r[1];
      m.put((String) r[0], n == null ? null : n.doubleValue());
    }
    return m;
  }

  Map<String, Instant> toInstantMap(List<Object[]> rows) {
    Map<String, Instant> m = new HashMap<>();
    for (Object[] r : rows) {
      m.put((String) r[0], (Instant) r[1]);
    }
    return m;
  }
}
