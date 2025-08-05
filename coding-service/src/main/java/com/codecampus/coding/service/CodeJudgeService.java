package com.codecampus.coding.service;

import com.codecampus.coding.dto.request.SubmissionRequestDto;
import com.codecampus.coding.dto.response.CodeResult;
import com.codecampus.coding.dto.response.SubmissionResponseDto;
import com.codecampus.coding.dto.response.SubmissionTestCaseResultDto;
import com.codecampus.coding.entity.CodeSubmission;
import com.codecampus.coding.entity.CodingExercise;
import com.codecampus.coding.entity.TestCase;
import com.codecampus.coding.entity.data.CodeSubmissionId;
import com.codecampus.coding.helper.CodingHelper;
import com.codecampus.coding.mapper.SubmissionMapper;
import com.codecampus.coding.repository.CodeSubmissionRepository;
import com.codecampus.submission.grpc.CodeSubmissionDto;
import com.codecampus.submission.grpc.CreateCodeSubmissionRequest;
import com.codecampus.submission.grpc.SubmissionSyncServiceGrpc;
import com.codecampus.submission.grpc.TestCaseResultSyncDto;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodeJudgeService {

    CodeSubmissionRepository codeSubmissionRepository;

    SubmissionMapper submissionMapper;

    CodingHelper codingHelper;

    DockerSandboxService dockerSandboxService;

    SubmissionSyncServiceGrpc.SubmissionSyncServiceBlockingStub
            submissionStub;

    @Transactional
    public SubmissionResponseDto judgeCodeSubmission(
            SubmissionRequestDto request) {

        CodingExercise codingExercise = codingHelper
                .findCodingOrThrow(request.exerciseId());

        List<TestCaseResultSyncDto> testCaseResultSyncDtoList =
                new ArrayList<>();
        List<SubmissionTestCaseResultDto> submissionTestCaseResultDtoList =
                new ArrayList<>();

        int passedCount = 0;

        for (TestCase testCase : codingExercise.getTestCases()) {
            CodeResult codeResult = dockerSandboxService.run(
                    request.sourceCode(),
                    request.language(),
                    testCase,
                    request.memoryMb(),
                    request.cpus());

            boolean passed = codeResult.isPassed();
            if (passed) {
                passedCount++;
            }

            CodeSubmission codeSubmission = CodeSubmission.builder()
                    .id(new CodeSubmissionId(request.submissionId(),
                            testCase.getId()))
                    .exercise(codingExercise)
                    .testCase(testCase)
                    .studentId(request.studentId())
                    .language(request.language())
                    .sourceCode(request.sourceCode())
                    .submittedAt(Instant.now())
                    .timeTakenSeconds(request.timeTakenSeconds())
                    .passed(passed)
                    .runtimeMs(codeResult.getRuntimeMs())
                    .memoryKb(codeResult.getMemoryKb())
                    .output(codeResult.getOutput())
                    .errorMessage(codeResult.getError())
                    .build();
            codeSubmissionRepository.save(codeSubmission);

            submissionTestCaseResultDtoList.add(new SubmissionTestCaseResultDto(
                    testCase.getInput(), testCase.getExpectedOutput(),
                    codeResult.getOutput(), passed, codeResult.getRuntimeMs()));

            // Sync
            testCaseResultSyncDtoList.add(TestCaseResultSyncDto.newBuilder()
                    .setTestCaseId(testCase.getId())
                    .setPassed(passed)
                    .setRuntimeMs(codeResult.getRuntimeMs())
                    .setMemoryKb(codeResult.getMemoryKb())
                    .setOutput(Optional.ofNullable(codeResult.getOutput())
                            .orElse(""))
                    .setErrorMessage(
                            Optional.ofNullable(codeResult.getError())
                                    .orElse(""))
                    .build());
        }

        CodeSubmissionDto codeSubmissionDto = CodeSubmissionDto.newBuilder()
                .setId(request.submissionId())
                .setExerciseId(request.exerciseId())
                .setStudentId(request.studentId())
                .setScore(passedCount)
                .setTotalPoints(codingExercise.getTestCases().size())
                .setLanguage(request.language())
                .setSourceCode(request.sourceCode())
                .setSubmittedAt(com.google.protobuf.Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .setTimeTakenSeconds(request.timeTakenSeconds())
                .addAllResults(testCaseResultSyncDtoList)
                .build();

        /* Sync tới submission service */
        submissionStub.createCodeSubmission(
                CreateCodeSubmissionRequest.newBuilder()
                        .setSubmission(codeSubmissionDto)
                        .build());

        return SubmissionResponseDto.builder()
                .submissionId(request.submissionId())
                .score(passedCount)
                .totalPoints(codingExercise.getTestCases().size())
                .passed(passedCount == codingExercise.getTestCases().size())
                .testCases(submissionTestCaseResultDtoList)
                .build();
    }
}

