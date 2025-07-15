package com.codecampus.coding.mapper;

import com.codecampus.coding.entity.CodingExercise;
import com.codecampus.coding.entity.TestCase;
import com.codecampus.coding.grpc.CodingDetailDto;
import com.codecampus.coding.grpc.CodingExerciseDto;
import com.codecampus.coding.grpc.TestCaseDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CodingMapper {
    CodingExerciseDto toCodingExerciseDtoFromCodingExercise(
            CodingExercise codingExercise);

    @Mapping(target = "allowedLanguages", ignore = true)
    CodingExercise toCodingExerciseFromCodingDetailDto(
            CodingDetailDto codingDetailDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchCodingExerciseDtoToCodingExercise(
            @MappingTarget CodingExercise codingExercise,
            CodingExerciseDto codingExerciseDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "allowedLanguages",
            expression = "java(codingDetailDto.getAllowedLanguagesList()==null ? null : new java.util.HashSet<>(codingDetailDto.getAllowedLanguagesList()))")
    void patchCodingDetailDtoToCodingExercise(
            @MappingTarget CodingExercise codingExercise,
            CodingDetailDto codingDetailDto);

    @Mapping(target = "exercise", ignore = true)
    TestCase toTestCaseFromTestCaseDto(TestCaseDto testCaseDto);

    @Mapping(target = "exerciseId", source = "exercise.id")
    TestCaseDto toTestCaseDtoFromTestCase(TestCase t);

    void patchTestCaseDtoToTestCase(
            @MappingTarget TestCase testCase,
            TestCaseDto testCaseDto);
}
