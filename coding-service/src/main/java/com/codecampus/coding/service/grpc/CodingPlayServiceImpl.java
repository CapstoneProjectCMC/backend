package com.codecampus.coding.service.grpc;

import com.codecampus.coding.dto.request.SubmissionRequestDto;
import com.codecampus.coding.dto.response.SubmissionResponseDto;
import com.codecampus.coding.grpc.CodingPlayServiceGrpc;
import com.codecampus.coding.grpc.SubmitCodeRequest;
import com.codecampus.coding.grpc.SubmitCodeResponse;
import com.codecampus.coding.grpc.TestCaseResultDto;
import com.codecampus.coding.service.CodeJudgeService;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodingPlayServiceImpl
        extends CodingPlayServiceGrpc.CodingPlayServiceImplBase {

    CodeJudgeService codeJudgeService;

    @Override
    public void submitCode(
            SubmitCodeRequest request,
            StreamObserver<SubmitCodeResponse> responseObserver) {

        SubmissionRequestDto submissionRequestDto =
                SubmissionRequestDto.builder()
                        .submissionId(request.getSubmissionId())
                        .exerciseId(request.getExerciseId())
                        .studentId(request.getStudentId())
                        .language(request.getLanguage())
                        .sourceCode(request.getSourceCode())
                        .memoryMb(request.getMemoryMb())
                        .cpus(request.getCpus())
                        .timeTakenSeconds(request.getTimeTakenSeconds())
                        .build();

        SubmissionResponseDto submissionResponseDto =
                codeJudgeService.judgeCodeSubmission(
                        submissionRequestDto);

        SubmitCodeResponse submitCodeResponse = SubmitCodeResponse.newBuilder()
                .setSubmissionId(submissionResponseDto.submissionId())
                .setScore(submissionResponseDto.score())
                .setTotalPoints(submissionResponseDto.totalPoints())
                .setPassed(submissionResponseDto.passed())
                .addAllResults(submissionResponseDto.testCases().stream()
                        .map(t -> TestCaseResultDto.newBuilder()
                                .setTestCaseId("")
                                .setPassed(t.passed())
                                .setRuntimeMs(t.executionTimeMs())
                                .setOutput(t.actualOutput())
                                .build())
                        .toList())
                .build();

        responseObserver.onNext(submitCodeResponse);
        responseObserver.onCompleted();
    }
}
