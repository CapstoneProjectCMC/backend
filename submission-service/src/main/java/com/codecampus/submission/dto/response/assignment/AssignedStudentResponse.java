package com.codecampus.submission.dto.response.assignment;

import java.time.Instant;

public record AssignedStudentResponse(
        String assignmentId,
        String studentId,
        Instant dueAt,
        boolean completed,
        Integer bestScore,  // best score của HS cho exercise này
        Integer totalPoints // tổng điểm tối đa (quiz) / số testcases (coding)
) {
}