package com.codecampus.submission.controller;

import com.codecampus.submission.constant.sort.SortField;
import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.request.coding.AddCodingDetailRequest;
import com.codecampus.submission.dto.request.coding.TestCaseDto;
import com.codecampus.submission.dto.request.coding.UpdateCodingDetailWithTestCaseRequest;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodingController {

    ExerciseService exerciseService;
    CodingService codingService;

    @PostMapping("/coding/exercise/{exerciseId}/coding-detail")
    ApiResponse<Void> addCodingDetail(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid AddCodingDetailRequest addCodingRequest) {

        codingService.addCodingDetail(
                exerciseId,
                addCodingRequest,
                false);

        return ApiResponse.<Void>builder()
                .message("Tạo Coding thành công!")
                .build();
    }

    @PatchMapping("/coding/exercise/{exerciseId}/coding-detail")
    ApiResponse<Void> updateCodingDetailWithTestCaseRequest(
            @PathVariable String exerciseId,
            @RequestBody UpdateCodingDetailWithTestCaseRequest dto) {

        codingService.updateCodingDetailWithTestCaseRequest(exerciseId, dto);

        return ApiResponse.<Void>builder()
                .message("Sửa Coding‑detail thành công!")
                .build();
    }

    @GetMapping("/coding/{exerciseId}/test-cases")
    ApiResponse<PageResponse<TestCase>> getTestCases(
            @PathVariable String exerciseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "CREATED_AT") SortField sortBy,
            @RequestParam(defaultValue = "false") boolean asc) {
        return ApiResponse.<PageResponse<TestCase>>builder()
                .message("Get các testcases thành công!")
                .result(codingService.getTestCases(
                        exerciseId,
                        page, size,
                        sortBy, asc))
                .build();
    }

    @GetMapping("/coding/test-case/{testCaseId}")
    ApiResponse<TestCase> getTestCaseById(
            @PathVariable String testCaseId) {
        return ApiResponse.<TestCase>builder()
                .result(codingService.getTestCaseOrThrow(testCaseId))
                .message("Chi tiết testcase!")
                .build();
    }

    @PostMapping("/coding/{exerciseId}/test-case")
    ApiResponse<TestCase> addTestCase(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid TestCaseDto testCaseDto)
            throws BadRequestException {

        codingService.addTestCase(
                exerciseId,
                testCaseDto,
                false);

        return ApiResponse.<TestCase>builder()
                .message("Thêm testcase cho coding thành công!")
                .build();
    }

    @PatchMapping("/coding/{exerciseId}/test-case/{testCaseId}")
    ApiResponse<Void> updateTestCase(
            @PathVariable String exerciseId,
            @PathVariable String testCaseId,
            @RequestBody UpdateTestCaseRequest request) {

        codingService.updateTestCase(exerciseId, testCaseId, request);

        return ApiResponse.<Void>builder()
                .message("Sửa testcase thành công!")
                .build();
    }

    @DeleteMapping("/coding/{exerciseId}/test-case/{testCaseId}")
    ApiResponse<Void> softDeleteTestCase(
            @PathVariable String exerciseId,
            @PathVariable String testCaseId) {
        codingService.softDeleteTestCase(exerciseId, testCaseId);

        return ApiResponse.<Void>builder()
                .message("Đã xoá testcase!")
                .build();
    }
}
