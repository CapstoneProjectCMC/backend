package com.codecampus.submission.service;

import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.response.AllSubmissionHistoryResponse;
import com.codecampus.submission.dto.response.coding.CodingAttemptHistoryResponse;
import com.codecampus.submission.dto.response.quiz.QuizAttemptHistoryResponse;
import com.codecampus.submission.helper.AuthenticationHelper;
import com.codecampus.submission.helper.PageResponseHelper;
import com.codecampus.submission.mapper.SubmissionMapper;
import com.codecampus.submission.repository.SubmissionRepository;
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
public class SubmissionHistoryService {

  SubmissionRepository submissionRepository;
  SubmissionMapper submissionMapper;

  @Transactional(readOnly = true)
  public PageResponse<QuizAttemptHistoryResponse> getQuizAttemptHistoriesForStudent(
      int page, int size) {

    String studentId = AuthenticationHelper.getMyUserId();

    Pageable pageable = PageRequest.of(
        Math.max(page - 1, 0),
        Math.max(size, 1),
        Sort.by(Sort.Order.desc("score"),
            Sort.Order.asc("timeTakenSeconds"),
            Sort.Order.desc("submittedAt"))
    );

    Page<QuizAttemptHistoryResponse> pageData = submissionRepository
        .findQuizSubmissionsByStudent(studentId, pageable)
        .map(submissionMapper::mapSubmissionToQuizAttemptHistoryResponse);

    return PageResponseHelper.toPageResponse(pageData, page);
  }

  @Transactional(readOnly = true)
  public PageResponse<CodingAttemptHistoryResponse> getCodingAttemptHistoriesForStudent(
      int page, int size) {

    String studentId = AuthenticationHelper.getMyUserId();

    Pageable pageable = PageRequest.of(
        Math.max(page - 1, 0),
        Math.max(size, 1),
        Sort.by(Sort.Order.desc("score"),
            Sort.Order.asc("timeTakenSeconds"),
            Sort.Order.desc("submittedAt"))
    );

    Page<CodingAttemptHistoryResponse> pageData =
        submissionRepository
            .findCodingSubmissionsByStudent(studentId, pageable)
            .map(submissionMapper::mapSubmissionToCodingAttemptHistoryResponse);

    return PageResponseHelper.toPageResponse(pageData, page);
  }

  @Transactional(readOnly = true)
  public PageResponse<AllSubmissionHistoryResponse> mySubmissions(
      int page, int size) {
    String studentId = AuthenticationHelper.getMyUserId();

    Pageable pageable = PageRequest.of(
        Math.max(page - 1, 0),
        Math.max(size, 1),
        // Mặc định: mới nhất trước
        Sort.by(Sort.Order.desc("submittedAt"))
    );

    Page<AllSubmissionHistoryResponse> pageData =
        submissionRepository
            .findByUserId(studentId, pageable)
            .map(submissionMapper::mapSubmissionToAllSubmissionHistoryResponse);

    return PageResponseHelper.toPageResponse(pageData, page);
  }
}
