package com.codecampus.submission.dto.response.coding;

import com.codecampus.submission.constant.submission.SubmissionStatus;

import java.time.Instant;

public record CodingAttemptHistoryResponse(
        String submissionId,
        String exerciseId,
        String exerciseTitle,
        Integer score,             // #testcases passed
        Integer totalPoints,       // tổng #testcases
        Integer timeTakenSeconds,
        String language,
        Integer peakMemoryMb,      // map từ Submission.memoryUsed
        SubmissionStatus status,
        Instant submittedAt
) {
}