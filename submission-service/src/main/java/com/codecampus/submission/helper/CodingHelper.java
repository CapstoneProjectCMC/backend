package com.codecampus.submission.helper;

import com.codecampus.submission.constant.sort.SortField;
import com.codecampus.submission.dto.request.coding.TestCasePatchDto;
import com.codecampus.submission.dto.response.coding.coding_detail.CodingDetailSliceDetailResponse;
import com.codecampus.submission.dto.response.coding.coding_detail.TestCaseDetailResponse;
import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.TestCase;
import com.codecampus.submission.repository.TestCaseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CodingHelper {

    TestCaseRepository testCaseRepository;


    public CodingDetailSliceDetailResponse buildCodingSlice(
            CodingDetail codingDetail,
            int page, int size,
            SortField sortBy, boolean asc) {

        Pageable pageable = PageRequest.of(
                page - 1, size, SortHelper.build(sortBy, asc));

        Page<TestCase> pageData =
                testCaseRepository.findByCodingDetailExerciseId(
                        codingDetail.getExercise().getId(), pageable);

        return CodingDetailSliceDetailResponse.builder()
                .id(codingDetail.getId())
                .topic(codingDetail.getTopic())
                .allowedLanguages(codingDetail.getAllowedLanguages())
                .input(codingDetail.getInput())
                .output(codingDetail.getOutput())
                .constraintText(codingDetail.getConstraintText())
                .timeLimit(codingDetail.getTimeLimit())
                .memoryLimit(codingDetail.getMemoryLimit())
                .maxSubmissions(codingDetail.getMaxSubmissions())
                .codeTemplate(codingDetail.getCodeTemplate())
                .solution(codingDetail.getSolution())

                .currentPage(pageData.getNumber() + 1)
                .totalPages(pageData.getTotalPages())
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())

                .testCases(pageData.getContent().stream()
                        .sorted(Comparator.comparing(TestCase::isSample)
                                .reversed())
                        .map(this::mapTestCaseDetailResponseFromTestCase)
                        .toList())

                .createdBy(codingDetail.getCreatedBy())
                .createdAt(codingDetail.getCreatedAt())
                .updatedBy(codingDetail.getUpdatedBy())
                .updatedAt(codingDetail.getUpdatedAt())
                .deletedBy(codingDetail.getDeletedBy())
                .deletedAt(codingDetail.getDeletedAt())
                .build();
    }

    TestCaseDetailResponse mapTestCaseDetailResponseFromTestCase(
            TestCase testCase) {
        return TestCaseDetailResponse.builder()
                .id(testCase.getId())
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .sample(testCase.isSample())
                .note(testCase.getNote())
                .createdBy(testCase.getCreatedBy())
                .createdAt(testCase.getCreatedAt())
                .updatedBy(testCase.getUpdatedBy())
                .updatedAt(testCase.getUpdatedAt())
                .deletedBy(testCase.getDeletedBy())
                .deletedAt(testCase.getDeletedAt())
                .build();
    }

    public void patchTestCasePatchDtoToTestCase(
            TestCase testCase,
            TestCasePatchDto testCasePatchDto) {
        if (testCasePatchDto.input() != null) {
            testCase.setInput(testCasePatchDto.input());
        }
        if (testCasePatchDto.expectedOutput() != null) {
            testCase.setExpectedOutput(testCasePatchDto.expectedOutput());
        }
        if (testCasePatchDto.sample() != null) {
            testCase.setSample(testCasePatchDto.sample());
        }
        if (testCasePatchDto.note() != null) {
            testCase.setNote(testCasePatchDto.note());
        }
    }
}
