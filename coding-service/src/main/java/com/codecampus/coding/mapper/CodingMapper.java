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
    CodingExerciseDto toCodingExerciseDto(CodingExercise e);

    @Mapping(target = "allowedLanguages", ignore = true)
    CodingExercise toCodingExercise(CodingDetailDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchCodingExerciseDto(@MappingTarget CodingExercise ex,
                                CodingExerciseDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "allowedLanguages",
            expression = "java(dto.getAllowedLanguagesList()==null ? null : new java.util.HashSet<>(dto.getAllowedLanguagesList()))")
    void patchCodingDetailDto(@MappingTarget CodingExercise ex,
                              CodingDetailDto dto);

    @Mapping(target = "exercise", ignore = true)
    TestCase toTestCase(TestCaseDto dto);

    @Mapping(target = "exerciseId", source = "exercise.id")
    TestCaseDto toTestCaseDto(TestCase t);

    void patchTestCaseDto(@MappingTarget TestCase testCase,
                          TestCaseDto dto);
}
