package com.codecampus.submission.service;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.request.coding.AddCodingDetailRequest;
import com.codecampus.submission.dto.request.coding.TestCaseDto;
import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.TestCase;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.mapper.TestCaseMapper;
import com.codecampus.submission.repository.CodingDetailRepository;
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.repository.TestCaseRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodingService {
    CodingDetailRepository codingDetailRepository;
    TestCaseRepository testCaseRepository;
    ExerciseRepository exerciseRepository;

    TestCaseMapper testCaseMapper;

    @Transactional
    public CodingDetail addCodingDetail(
            String exerciseId,
            AddCodingDetailRequest addCodingRequest) {

        Exercise exercise = getExerciseOrThrow(exerciseId);
        Assert.isTrue(
                exercise.getExerciseType() == ExerciseType.CODING,
                "Exercise không phải CODE"
        );

        if (exercise.getCodingDetail() != null) {
            throw new AppException(ErrorCode.EXERCISE_TYPE);
        }

        CodingDetail codingDetail = CodingDetail.builder()
                .exercise(exercise)
                .topic(addCodingRequest.topic())
                .allowedLanguages(addCodingRequest.allowedLanguages())
                .input(addCodingRequest.input())
                .output(addCodingRequest.output())
                .constraintText(addCodingRequest.constraintText())
                .timeLimit(addCodingRequest.timeLimit())
                .memoryLimit(addCodingRequest.memoryLimit())
                .maxSubmissions(addCodingRequest.maxSubmissions())
                .codeTemplate(addCodingRequest.codeTemplate())
                .build();

        addCodingRequest.testCases().forEach(tcDto -> {
            TestCase testCase = testCaseMapper.toTestCase(tcDto);
            testCase.setCodingDetail(codingDetail);
            codingDetail.getTestCases().add(testCase);
        });
        return codingDetailRepository.save(codingDetail);
    }

    @Transactional
    public TestCase addTestCase(
            String exerciseId,
            TestCaseDto testCaseDto) throws BadRequestException {

        Exercise exercise = getExerciseOrThrow(exerciseId);
        CodingDetail codingDetail = Optional
                .ofNullable(exercise.getCodingDetail())
                .orElseThrow(
                        () -> new BadRequestException("Chưa có CodingDetail")
                );

        TestCase testCase = testCaseMapper.toTestCase(testCaseDto);
        testCase.setCodingDetail(codingDetail);
        codingDetail.getTestCases().add(testCase);

        return testCaseRepository.save(testCase);
    }

    Exercise getExerciseOrThrow(String exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND));
    }
}
