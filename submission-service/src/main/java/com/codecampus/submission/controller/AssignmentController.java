package com.codecampus.submission.controller;

import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.request.assignment.BulkAssignExerciseRequest;
import com.codecampus.submission.dto.response.assignment.BulkAssignExerciseResponse;
import com.codecampus.submission.entity.Assignment;
import com.codecampus.submission.service.AssignmentService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentController {

    AssignmentService assignmentService;

    @PostMapping("/assignment")
    ApiResponse<Assignment> assignExercise(
            @RequestParam String exerciseId,
            @RequestParam String studentId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dueAt) {
        return ApiResponse.<Assignment>builder()
                .result(assignmentService.assignExercise(exerciseId, studentId,
                        dueAt))
                .message("Giao bài thành công!")
                .build();
    }

    @PostMapping("/assignment-bulk")
    ApiResponse<BulkAssignExerciseResponse> bulkAssignExercise(
            @RequestParam String exerciseId,
            @RequestBody BulkAssignExerciseRequest bulkAssignExerciseRequest) {

        List<Assignment> assignmentList =
                assignmentService.assignExerciseToMany(
                        exerciseId,
                        bulkAssignExerciseRequest);

        long studentsAssignments = assignmentList.stream()
                .filter(assignment -> assignment.getCreatedAt() != null
                        && assignment.getUpdatedAt() != null).count();

        BulkAssignExerciseResponse bulkAssignExerciseResponse =
                BulkAssignExerciseResponse.builder()
                        .exerciseId(exerciseId)
                        .studentsAssigned(studentsAssignments)
                        .dueAt(bulkAssignExerciseRequest.dueAt())
                        .build();

        return ApiResponse.<BulkAssignExerciseResponse>builder()
                .result(bulkAssignExerciseResponse)
                .message("Giao bài thành công!")
                .build();
    }
}
