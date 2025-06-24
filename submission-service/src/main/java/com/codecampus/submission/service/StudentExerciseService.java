package com.codecampus.submission.service;

import static com.codecampus.submission.utils.PageResponseUtils.toPageResponse;

import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.response.contest.ContestResponse;
import com.codecampus.submission.dto.response.exercise.AssignmentResponse;
import com.codecampus.submission.dto.response.exercise.ExerciseSummaryResponse;
import com.codecampus.submission.dto.response.exercise.SubmissionResponse;
import com.codecampus.submission.mapper.AssignmentMapper;
import com.codecampus.submission.mapper.ContestMapper;
import com.codecampus.submission.mapper.ExerciseMapper;
import com.codecampus.submission.mapper.SubmissionMapper;
import com.codecampus.submission.repository.AssignmentRepository;
import com.codecampus.submission.repository.ContestRepository;
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.repository.SubmissionRepository;
import com.codecampus.submission.repository.specification.ExerciseSpecification;
import com.codecampus.submission.utils.SecurityUtils;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentExerciseService {

  ExerciseRepository exerciseRepository;
  AssignmentRepository assignmentRepository;
  ContestRepository contestRepository;
  SubmissionRepository submissionRepository;

  ExerciseMapper exerciseMapper;
  AssignmentMapper assignmentMapper;
  ContestMapper contestMapper;
  SubmissionMapper submissionMapper;

  // Có thể public
  public PageResponse<ExerciseSummaryResponse> getExercise(
      String keyword, Integer diff,
      int page, int size) {

    Pageable pageable = PageRequest.of(page - 1, size);

    var spec = ExerciseSpecification.build(keyword, diff);

    var pageData = exerciseRepository
        .findAll(spec, pageable)
        .map(exerciseMapper::toExerciseSummaryResponse);

    return toPageResponse(pageData, page);
  }

  public PageResponse<AssignmentResponse> getMyAssignments(
      int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);

    var pageData = assignmentRepository
        .findByStudentId(SecurityUtils.getMyUserId(), pageable)
        .map(assignmentMapper::toAssignmentResponse);

    return toPageResponse(pageData, page);
  }

  public PageResponse<ContestResponse> getContests(
      int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);

    var pageData = contestRepository
        .findIncomingContests(Instant.now(), pageable)
        .map(contestMapper::toContestResponse);

    return toPageResponse(pageData, page);
  }

  public PageResponse<SubmissionResponse> getMySubmissions(
      int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = submissionRepository
        .findByUserId(SecurityUtils.getMyUserId(), pageable)
        .map(submissionMapper::toSubmissionResponse);

    return toPageResponse(pageData, page);
  }
}
