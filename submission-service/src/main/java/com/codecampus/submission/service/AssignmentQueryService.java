package com.codecampus.submission.service;

import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.response.assignment.AssignedStudentResponse;
import com.codecampus.submission.dto.response.assignment.MyAssignmentResponse;
import com.codecampus.submission.entity.Assignment;
import com.codecampus.submission.helper.AssignmentHelper;
import com.codecampus.submission.helper.AuthenticationHelper;
import com.codecampus.submission.helper.PageResponseHelper;
import com.codecampus.submission.repository.AssignmentRepository;
import com.codecampus.submission.service.cache.UserBulkLoader;
import dtos.UserSummary;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentQueryService {

  AssignmentRepository assignmentRepository;

  AssignmentHelper assignmentHelper;
  UserBulkLoader userBulkLoader;

  @Transactional(readOnly = true)
  public PageResponse<MyAssignmentResponse> getAssignmentsForStudent(
      int page, int size) {

    String studentId = AuthenticationHelper.getMyUserId();

    // Sort mặc định: dueAt DESC rồi createdAt DESC
    Pageable pageable = PageRequest.of(
        Math.max(page - 1, 0),
        Math.max(size, 1),
        Sort.by(Sort.Order.desc("dueAt"), Sort.Order.desc("createdAt"))
    );

    Page<MyAssignmentResponse> pageData = assignmentRepository
        .findByStudentId(studentId, pageable)
        .map(assignment -> assignmentHelper.mapAssignmentToMyAssignmentResponse(
            assignment, studentId));

    return PageResponseHelper.toPageResponse(pageData, page);
  }

  @Transactional(readOnly = true)
  public PageResponse<AssignedStudentResponse> getAssignedStudentsForExercise(
      String exerciseId, Boolean completed,
      int page, int size) {

    // Sort mặc định: dueAt DESC rồi createdAt DESC
    Pageable pageable = PageRequest.of(
        Math.max(page - 1, 0),
        Math.max(size, 1),
        Sort.by(Sort.Order.desc("dueAt"), Sort.Order.desc("createdAt"))
    );

    Page<Assignment> assignmentPage = (
        completed == null
            ? assignmentRepository.findByExerciseId(exerciseId,
            pageable)
            : assignmentRepository.findByExerciseIdAndCompleted(
            exerciseId, completed, pageable));
    //.map(assignment -> assignmentHelper.mapAssignmentToAssignedStudentResponse(
    //        assignment, assignment.getStudentId()));

    // Bulk load UserSummary cho studentIds ở trang hiện tại
    Set<String> studentIds = assignmentPage.getContent()
        .stream()
        .map(Assignment::getStudentId)
        .collect(Collectors.toSet());

    Map<String, UserSummary> summaries = userBulkLoader.loadAll(studentIds);

    Page<AssignedStudentResponse> pageData = assignmentPage
        .map(a -> assignmentHelper
            .mapAssignmentToAssignedStudentResponse(
                a, summaries.get(a.getStudentId())));

    return PageResponseHelper.toPageResponse(pageData, page);
  }

}
