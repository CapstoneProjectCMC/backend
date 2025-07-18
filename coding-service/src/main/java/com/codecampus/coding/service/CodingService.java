package com.codecampus.coding.service;

import com.codecampus.coding.entity.CodingExercise;
import com.codecampus.coding.entity.TestCase;
import com.codecampus.coding.grpc.AddCodingDetailRequest;
import com.codecampus.coding.grpc.AddTestCaseRequest;
import com.codecampus.coding.grpc.CodingDetailDto;
import com.codecampus.coding.grpc.CodingExerciseDto;
import com.codecampus.coding.grpc.CreateCodingExerciseRequest;
import com.codecampus.coding.grpc.TestCaseDto;
import com.codecampus.coding.helper.CodingHelper;
import com.codecampus.coding.mapper.CodingMapper;
import com.codecampus.coding.repository.CodingExerciseRepository;
import com.codecampus.coding.repository.TestCaseRepository;
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
    TestCaseRepository testCaseRepository;

    CodingMapper codingMapper;

    CodingHelper codingHelper;

    @Transactional
    public void createCodingExercise(
            CreateCodingExerciseRequest createCodingRequest) {

        CodingExerciseDto exerciseDto = createCodingRequest.getExercise();
        CodingExercise codingExercise = codingExerciseRepository
                .findById(exerciseDto.getId())
                .orElseGet(CodingExercise::new);

        codingMapper.patchCodingExerciseDtoToCodingExercise(codingExercise,
                exerciseDto);
        codingExerciseRepository.save(codingExercise);
    }

    @Transactional
    public void addCodingDetail(
            AddCodingDetailRequest addCodingRequest) {

        CodingDetailDto codingDetailDto = addCodingRequest.getDetail();

        CodingExercise codingExercise =
                codingHelper.findCodingOrThrow(codingDetailDto.getExerciseId());

        codingMapper.patchCodingDetailDtoToCodingExercise(codingExercise,
                codingDetailDto);

        codingExerciseRepository.save(codingExercise);
    }

    @Transactional
    public void addTestCase(AddTestCaseRequest addTestCaseRequest) {
        TestCaseDto testCaseDto = addTestCaseRequest.getTestCase();
        CodingExercise coding =
                codingHelper.findCodingOrThrow(testCaseDto.getExerciseId());

        TestCase testCase = coding.getTestCases().stream()
                .filter(t -> t.getId().equals(testCaseDto.getId()))
                .findFirst()
                .orElseGet(() -> {
                    TestCase newTestCase =
                            codingMapper.toTestCaseFromTestCaseDto(testCaseDto);
                    newTestCase.setExercise(coding);
                    coding.getTestCases().add(newTestCase);
                    return newTestCase;
                });

        codingMapper.patchTestCaseDtoToTestCase(testCase, testCaseDto);
        testCaseRepository.save(testCase);
    }
}
