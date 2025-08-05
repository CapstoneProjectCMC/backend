package com.codecampus.coding.service;

import com.codecampus.coding.entity.CodingExercise;
import com.codecampus.coding.entity.TestCase;
import com.codecampus.coding.grpc.AddCodingDetailRequest;
import com.codecampus.coding.grpc.AddTestCaseRequest;
import com.codecampus.coding.grpc.CodingExerciseDto;
import com.codecampus.coding.grpc.CreateCodingExerciseRequest;
import com.codecampus.coding.grpc.TestCaseDto;
import com.codecampus.coding.helper.AuthenticationHelper;
import com.codecampus.coding.helper.CodingHelper;
import com.codecampus.coding.mapper.CodingMapper;
import com.codecampus.coding.repository.CodingExerciseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodingService {

    CodingExerciseRepository codingExerciseRepository;

    CodingMapper codingMapper;

    CodingHelper codingHelper;

    @Transactional
    public void createCodingExercise(
            CreateCodingExerciseRequest createCodingRequest) {

        CodingExerciseDto exerciseDto = createCodingRequest.getExercise();
        CodingExercise codingExercise = codingExerciseRepository
                .findById(exerciseDto.getId())
                .orElseGet(CodingExercise::new);

        codingMapper.patchCodingExerciseDtoToCodingExercise(
                exerciseDto, codingExercise);
        codingExerciseRepository.save(codingExercise);
    }

    @Transactional
    public void addCodingDetail(
            AddCodingDetailRequest addCodingRequest) {

        CodingExercise coding =
                codingHelper.findCodingOrThrow(
                        addCodingRequest.getExerciseId());

        codingMapper.patchCodingDetailDtoToCodingExercise(
                coding, addCodingRequest.getCodingDetail());

        addCodingRequest.getCodingDetail().getTestcasesList()
                .forEach(testCaseDto -> {
                    TestCase testCase = coding.getTestCases()
                            .stream()
                            .filter(tc -> tc.getId()
                                    .equals(testCaseDto.getId()))
                            .findFirst()
                            .orElseGet(() -> {
                                TestCase newTestCase =
                                        codingMapper.toTestCaseFromTestCaseDto(
                                                testCaseDto
                                        );

                                newTestCase.setCoding(coding);
                                coding.getTestCases().add(newTestCase);
                                return newTestCase;
                            });

                    codingMapper.patchTestCaseDtoToTestCase(
                            testCaseDto, testCase);
                });

        codingExerciseRepository.save(coding);
    }

    @Transactional
    public void softDeleteExercise(String exerciseId) {
        CodingExercise codingExercise =
                codingHelper.findCodingOrThrow(exerciseId);
        String by = AuthenticationHelper.getMyUsername();
        codingExercise.markDeleted(by);
        codingExercise.getTestCases().forEach(tc -> {
            tc.markDeleted(by);
        });
        codingExerciseRepository.save(codingExercise);
    }

    @Transactional
    public void addTestCase(
            AddTestCaseRequest addTestCaseRequest) {
        CodingExercise coding =
                codingHelper.findCodingOrThrow(
                        addTestCaseRequest.getExerciseId());
        TestCaseDto testCaseDto = addTestCaseRequest.getTestCase();

        TestCase testCase = coding.findTestCaseById(testCaseDto.getId())
                .orElseGet(() -> {
                    TestCase tc = codingMapper.toTestCaseFromTestCaseDto(
                            testCaseDto);
                    tc.setCoding(coding);
                    coding.getTestCases().add(tc);
                    return tc;
                });

        codingMapper.patchTestCaseDtoToTestCase(testCaseDto, testCase);

        codingExerciseRepository.save(coding);
    }

    @Transactional
    public void softDeleteTestCase(
            String exerciseId,
            String testCaseId) {

        CodingExercise codingExercise = codingHelper
                .findCodingOrThrow(exerciseId);
        codingExercise.findTestCaseById(testCaseId).ifPresent(testCase -> {
            testCase.markDeleted(AuthenticationHelper.getMyUsername());
        });
    }
}
