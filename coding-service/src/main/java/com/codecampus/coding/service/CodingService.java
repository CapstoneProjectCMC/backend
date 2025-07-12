package com.codecampus.coding.service;

import com.codecampus.coding.entity.CodingExercise;
import com.codecampus.coding.entity.TestCase;
import com.codecampus.coding.exception.AppException;
import com.codecampus.coding.exception.ErrorCode;
import com.codecampus.coding.grpc.AddCodingDetailRequest;
import com.codecampus.coding.grpc.AddTestCaseRequest;
import com.codecampus.coding.grpc.CodingDetailDto;
import com.codecampus.coding.grpc.CodingExerciseDto;
import com.codecampus.coding.grpc.CreateCodingExerciseRequest;
import com.codecampus.coding.grpc.TestCaseDto;
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

    @Transactional
    public void createCodingExercise(
            CreateCodingExerciseRequest createCodingRequest) {

        CodingExerciseDto exerciseDto = createCodingRequest.getExercise();
        CodingExercise codingExercise = codingExerciseRepository
                .findById(exerciseDto.getId())
                .orElseGet(CodingExercise::new);

        codingMapper.patchCodingExerciseDto(codingExercise, exerciseDto);
        codingExerciseRepository.save(codingExercise);
    }

    @Transactional
    public void addCodingDetail(
            AddCodingDetailRequest addCodingRequest) {

        CodingDetailDto codingDetailDto = addCodingRequest.getDetail();

        CodingExercise codingExercise =
                findCodingOrThrow(codingDetailDto.getExerciseId());

        codingMapper.patchCodingDetailDto(codingExercise, codingDetailDto);

        codingExerciseRepository.save(codingExercise);
    }

    @Transactional
    public void addTestCase(AddTestCaseRequest req) {
        TestCaseDto dto = req.getTestCase();
        CodingExercise coding = findCodingOrThrow(dto.getExerciseId());

        TestCase tc = coding.getTestCases().stream()
                .filter(t -> t.getId().equals(dto.getId()))
                .findFirst()
                .orElseGet(() -> {
                    TestCase nt = codingMapper.toTestCase(dto);
                    nt.setExercise(coding);
                    coding.getTestCases().add(nt);
                    return nt;
                });

        codingMapper.patchTestCaseDto(tc, dto);
        testCaseRepository.save(tc);
    }

    public CodingExercise findCodingOrThrow(String exerciseId) {
        return codingExerciseRepository
                .findById(exerciseId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
                );
    }
}
