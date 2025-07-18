package com.codecampus.submission.mapper;

import com.codecampus.coding.grpc.CodingDetailDto;
import com.codecampus.coding.grpc.CodingExerciseDto;
import com.codecampus.coding.grpc.TestCaseDto;
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
    @Mapping(target = "exerciseId", source = "exercise.id")
    CodingDetailDto toCodingDetailDtoFromCodingDetail(
            CodingDetail codingDetail);

    @Mapping(target = "exerciseId", source = "codingDetail.exercise.id")
    TestCaseDto toTestCaseDtoFromTestCase(
            TestCase testCase);

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

    default CodingExerciseDto toCodingExerciseDtoFromExercise(Exercise e) {
        return CodingExerciseDto.newBuilder()
                .setId(e.getId())
                .setTitle(e.getTitle())
                .setDescription(
                        Optional.ofNullable(e.getDescription()).orElse(""))
                .build();
    }
}
