package com.codecampus.submission.dto.response.stats;


import lombok.Builder;

@Builder
public record SummaryStat(
    long totalExercises,
    long totalVisibleExercises,
    long totalQuiz,
    long totalCoding,
    long totalAssignments,
    long totalCompletedAssignments,
    long totalSubmissions,
    long totalPassedSubmissions
) {
}