package com.codecampus.submission.service;

import com.codecampus.submission.dto.response.AllSubmissionHistoryResponse;
import com.codecampus.submission.dto.response.quiz.QuizAttemptHistoryResponse;
import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.helper.AuthenticationHelper;
import com.codecampus.submission.mapper.SubmissionMapper;
import com.codecampus.submission.repository.SubmissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionHistoryService {

    SubmissionRepository submissionRepository;

    SubmissionMapper submissionMapper;

    @Transactional(readOnly = true)
    public List<QuizAttemptHistoryResponse> getQuizAttemptHistoriesForStudent() {
        return submissionRepository
                .findQuizSubmissionsByStudent(
                        AuthenticationHelper.getMyUserId())
                .stream()
                .sorted(Comparator
                        .comparing(Submission::getScore,
                                Comparator.nullsFirst(Integer::compareTo))
                        .reversed()
                        .thenComparing(s -> Optional.ofNullable(
                                s.getTimeTakenSeconds()).orElse(
                                Integer.MAX_VALUE)))
                .map(submissionMapper::mapSubmissionToQuizAttemptHistoryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AllSubmissionHistoryResponse> mySubmissions() {
        String studentId = AuthenticationHelper.getMyUserId();
        return submissionRepository.findByUserId(studentId).stream()
                .map(submissionMapper::mapSubmissionToAllSubmissionHistoryResponse)
                .toList();
    }
}
