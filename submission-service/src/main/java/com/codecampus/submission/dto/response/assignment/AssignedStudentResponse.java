package com.codecampus.submission.dto.response.assignment;

import com.codecampus.submission.constant.submission.ExerciseType;
import dtos.UserSummary;

import java.time.Instant;

public record AssignedStudentResponse(
        String assignmentId,
        UserSummary student,
        Instant dueAt,
        boolean completed,
        Instant completedAt,
        Integer bestScore,  // best score của HS cho exercise này
        Integer totalPoints, // tổng điểm tối đa (quiz) / số testcases (coding)
        ExerciseType exerciseType,
        Boolean pass
) {
}