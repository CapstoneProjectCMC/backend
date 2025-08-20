package com.codecampus.submission.mapper;

import com.codecampus.coding.grpc.CodingDetailDto;
import com.codecampus.coding.grpc.CodingExerciseDto;
import com.codecampus.coding.grpc.TestCaseDto;
import com.codecampus.submission.dto.request.coding.AddCodingDetailRequest;
import com.codecampus.submission.dto.request.coding.UpdateCodingDetailRequest;
import com.codecampus.submission.dto.request.coding.UpdateTestCaseRequest;
import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.TestCase;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface CodingMapper {

    private static String safeCheckNullString(String s) {
        return s == null ? "" : s;
    }

    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "testCases", ignore = true)
    CodingDetail toCodingDetailFromAddCodingRequest(
            AddCodingDetailRequest addCodingDetailRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchCodingDetailDtoToCodingDetail(
            @MappingTarget CodingDetail codingDetail,
            CodingDetailDto codingDetailDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchTestCaseDtoToTestCase(
            @MappingTarget TestCase testCase,
            TestCaseDto testCaseDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchUpdateCodingDetailRequestToCodingDetail(
            @MappingTarget CodingDetail codingDetail,
            UpdateCodingDetailRequest updateCodingDetailRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchUpdateTestCaseRequestToTestCase(
            UpdateTestCaseRequest request,
            @MappingTarget TestCase testCase);

    @Mapping(target = "exerciseId", source = "exercise.id")
    default CodingDetailDto toCodingDetailDtoFromCodingDetail(
            CodingDetail codingDetail) {
        return CodingDetailDto.newBuilder()
                .setExerciseId(codingDetail.getExercise().getId())
                .addAllAllowedLanguages(codingDetail.getAllowedLanguages())
                .setInput(codingDetail.getInput())
                .setOutput(codingDetail.getOutput())
                .setTimeLimit(codingDetail.getTimeLimit())
                .setTopic(codingDetail.getTopic())
                .setConstraintText(codingDetail.getConstraintText())
                .setMemoryLimit(codingDetail.getMemoryLimit())
                .setMaxSubmissions(codingDetail.getMaxSubmissions())
                .setCodeTemplate(codingDetail.getCodeTemplate())
                .setSolution(codingDetail.getSolution())
                .addAllTestcases(codingDetail.getTestCases()
                        .stream()
                        .map(this::toTestCaseDtoFromTestCase)
                        .toList()
                )
                .build();
    }

    default TestCaseDto toTestCaseDtoFromTestCase(
            TestCase testCase) {
        if (testCase == null) {
            return TestCaseDto.getDefaultInstance();
        }
        CodingDetail codingDetail = testCase.getCodingDetail();
        Exercise exercise =
                (codingDetail == null) ? null : codingDetail.getExercise();

        return TestCaseDto.newBuilder()
                .setId(safeCheckNullString(testCase.getId()))
                .setExerciseId(
                        safeCheckNullString(
                                exercise == null ? null : exercise.getId()))
                .setInput(safeCheckNullString(testCase.getInput()))
                .setExpectedOutput(
                        safeCheckNullString(testCase.getExpectedOutput()))
                .setSample(testCase.isSample())
                .setNote(safeCheckNullString(testCase.getNote()))
                .build();
    }

    default CodingExerciseDto toCodingExerciseDtoFromExercise(
            Exercise exercise) {
        return CodingExerciseDto.newBuilder()
                .setId(exercise.getId())
                .setTitle(exercise.getTitle())
                .setDescription(
                        Optional.ofNullable(exercise.getDescription())
                                .orElse(""))
                .setPublicAccessible(
                        exercise.isVisibility())        // reuse visibility
                .setCreatedBy(
                        Optional.ofNullable(exercise.getCreatedBy()).orElse(""))
                .build();
    }
}
