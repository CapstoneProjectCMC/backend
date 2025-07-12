package com.codecampus.submission.controller;

import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.request.coding.AddCodingDetailRequest;
import com.codecampus.submission.dto.request.coding.TestCaseDto;
import com.codecampus.submission.dto.request.coding.UpdateCodingDetailRequest;
import com.codecampus.submission.dto.request.coding.UpdateTestCaseRequest;
import com.codecampus.submission.entity.TestCase;
import com.codecampus.submission.service.CodingService;
import com.codecampus.submission.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/coding")
public class CodingController {
    ExerciseService exerciseService;
    CodingService codingService;

    @PostMapping("/exercise/{exerciseId}/coding-detail")
    ApiResponse<Void> addCodingDetail(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid AddCodingDetailRequest addCodingRequest) {

        codingService.addCodingDetail(exerciseId, addCodingRequest);

        return ApiResponse.<Void>builder()
                .message("Tạo Coding thành công!")
                .build();
    }

    @PatchMapping("/exercise/{exerciseId}/coding-detail")
    ApiResponse<Void> updateCodingDetail(
            @PathVariable String exerciseId,
            @RequestBody UpdateCodingDetailRequest dto) {

        codingService.updateCodingDetail(exerciseId, dto);

        return ApiResponse.<Void>builder()
                .message("Sửa Coding‑detail thành công!")
                .build();
    }

    @PostMapping("/{exerciseId}/coding/test-cases")
    ApiResponse<TestCase> addTestCase(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid TestCaseDto testCaseDto)
            throws BadRequestException {

        codingService.addTestCase(exerciseId, testCaseDto);

        return ApiResponse.<TestCase>builder()
                .message("Thêm testcase cho coding thành công!")
                .build();
    }

    @PatchMapping("/coding/test-case/{testCaseId}")
    ApiResponse<Void> updateTestCase(
            @PathVariable String testCaseId,
            @RequestBody UpdateTestCaseRequest dto) {

        codingService.updateTestCase(testCaseId, dto);

        return ApiResponse.<Void>builder()
                .message("Sửa Test‑case thành công!")
                .build();
    }
}
