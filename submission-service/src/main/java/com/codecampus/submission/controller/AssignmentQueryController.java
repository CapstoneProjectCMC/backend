package com.codecampus.submission.controller;

import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.response.assignment.MyAssignmentResponse;
import com.codecampus.submission.service.AssignmentQueryService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentQueryController {

    AssignmentQueryService assignmentQueryService;

    @GetMapping("/assignments/self")
    ApiResponse<List<MyAssignmentResponse>> myAssignments() {
        return ApiResponse.<List<MyAssignmentResponse>>builder()
                .result(assignmentQueryService.getAssignmentsForStudent())
                .message("Danh sách bài tập được giao cho bạn!")
                .build();
    }
}
