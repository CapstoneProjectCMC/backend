package com.codecampus.submission.controller;

import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.request.coding.AddCodingDetailRequest;
import com.codecampus.submission.dto.request.coding.TestCaseDto;
import com.codecampus.submission.entity.CodingDetail;
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
    ApiResponse<CodingDetail> addCodingDetail(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid AddCodingDetailRequest addCodingRequest) {
        return ApiResponse.<CodingDetail>builder()
                .result(codingService.addCodingDetail(exerciseId,
                        addCodingRequest))
                .message("Tạo Coding thành công!")
                .build();
    }

    @PostMapping("/{exerciseId}/coding/test-cases")
    ApiResponse<TestCase> addTestCase(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid TestCaseDto testCaseDto)
            throws BadRequestException {
        return ApiResponse.<TestCase>builder()
                .result(codingService.addTestCase(exerciseId, testCaseDto))
                .message("Thêm testcase cho coding thành công!")
                .build();
    }
}
