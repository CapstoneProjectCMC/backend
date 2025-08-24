package com.codecampus.coding.mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.codecampus.coding.entity.CodingExercise;
import com.codecampus.coding.entity.TestCase;
import com.codecampus.coding.grpc.CodingDetailDto;
import com.codecampus.coding.grpc.CodingDetailLoadResponse;
import com.codecampus.coding.grpc.CodingExerciseDto;
import com.codecampus.coding.grpc.LoadCodingResponse;
import com.codecampus.coding.grpc.TestCaseDto;
import com.codecampus.coding.grpc.TestCaseDtoLoadResponse;
import java.util.Comparator;
import java.util.Optional;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CodingMapper {
  CodingExerciseDto toCodingExerciseDtoFromCodingExercise(
      CodingExercise codingExercise);

  @Mapping(target = "allowedLanguages", ignore = true)
  CodingExercise toCodingExerciseFromCodingDetailDto(
      CodingDetailDto codingDetailDto);

  @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
  void patchCodingExerciseDtoToCodingExercise(
      CodingExerciseDto codingExerciseDto,
      @MappingTarget CodingExercise codingExercise);

  @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
  @Mapping(target = "allowedLanguages",
      expression = "java(codingDetailDto.getAllowedLanguagesList()==null ? null : new java.util.HashSet<>(codingDetailDto.getAllowedLanguagesList()))")
  void patchCodingDetailDtoToCodingExercise(
      @MappingTarget CodingExercise codingExercise,
      CodingDetailDto codingDetailDto);

  @Mapping(target = "coding", ignore = true)
  TestCase toTestCaseFromTestCaseDto(TestCaseDto testCaseDto);

  @Mapping(target = "exerciseId", source = "coding.id")
  TestCaseDto toTestCaseDtoFromTestCase(TestCase t);

  void patchTestCaseDtoToTestCase(
      TestCaseDto testCaseDto,
      @MappingTarget TestCase testCase);

  default LoadCodingResponse toLoadCodingResponseFromCodingExercise(
      CodingExercise codingExercise) {
    return LoadCodingResponse.newBuilder()
        .setExercise(
            toCodingExerciseDtoFromCodingExercise(codingExercise))
        .setDetail(toCodingDetailLoadResponseFromCodingExercise(
            codingExercise))
        .build();
  }

  default CodingDetailLoadResponse toCodingDetailLoadResponseFromCodingExercise(
      CodingExercise codingExercise) {
    return CodingDetailLoadResponse.newBuilder()
        .setTopic(Optional.ofNullable(codingExercise.getTopic())
            .orElse(""))
        .addAllAllowedLanguages(codingExercise.getAllowedLanguages())
        .setInput(Optional.ofNullable(codingExercise.getInput())
            .orElse(""))
        .setOutput(Optional.ofNullable(codingExercise.getOutput())
            .orElse(""))
        .setConstraintText(
            Optional.ofNullable(codingExercise.getConstraintText())
                .orElse(""))
        .setTimeLimit(codingExercise.getTimeLimit())
        .setMemoryLimit(codingExercise.getMemoryLimit())
        .setMaxSubmissions(codingExercise.getMaxSubmissions())
        .setCodeTemplate(
            Optional.ofNullable(codingExercise.getCodeTemplate())
                .orElse(""))
        .addAllTestcases(codingExercise.getTestCases()
            .stream()
            .sorted(Comparator.comparing(TestCase::isSample)
                .reversed())
            .map(this::toTestCaseDtoLoadResponseFromTestCase)
            .toList())
        .build();
  }

  default TestCaseDtoLoadResponse toTestCaseDtoLoadResponseFromTestCase(
      TestCase testCase) {
    return TestCaseDtoLoadResponse.newBuilder()
        .setId(testCase.getId())
        .setInput(testCase.getInput())
        .setExpectedOutput(
            testCase.isSample() ? testCase.getExpectedOutput() :
                "") // che
        .setSample(testCase.isSample())
        .setNote(Optional.ofNullable(testCase.getNote()).orElse(""))
        .build();
  }
}
