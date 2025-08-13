package com.codecampus.submission.controller;

import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.request.coding.AddCodingDetailRequest;
import com.codecampus.submission.dto.request.coding.TestCaseDto;
import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.TestCase;
import com.codecampus.submission.service.CodingService;
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
@RequestMapping("/internal")
public class InternalCodingController {

    CodingService codingService;

    @PostMapping("/coding/exercise/{exerciseId}/coding-detail")
    ApiResponse<CodingDetail> internalAddCodingDetail(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid AddCodingDetailRequest addCodingDetailRequest) {

        return ApiResponse.<CodingDetail>builder()
                .message("Tạo Quiz thành công!")
                .result(codingService.addCodingDetail(
                        exerciseId, addCodingDetailRequest, true))
                .build();
    }

    @PostMapping("/coding/{exerciseId}/test-case")
    ApiResponse<TestCase> internalAddTestCase(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid TestCaseDto testCaseDto)
            throws BadRequestException {

        return ApiResponse.<TestCase>builder()
                .message("Thêm câu hỏi cho quiz thành công!")
                .result(codingService.addTestCase(
                        exerciseId, testCaseDto, true))
                .build();
    }
}
