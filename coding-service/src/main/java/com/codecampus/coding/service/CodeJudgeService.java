package com.codecampus.coding.service;

import com.codecampus.coding.dto.response.CodeResult;
import com.codecampus.coding.dto.response.CompiledArtifact;
import com.codecampus.coding.entity.CodeSubmission;
import com.codecampus.coding.entity.CodeSubmissionResult;
import com.codecampus.coding.entity.CodingExercise;
import com.codecampus.coding.entity.TestCase;
import com.codecampus.coding.grpc.SubmitCodeRequest;
import com.codecampus.coding.grpc.SubmitCodeResponse;
import com.codecampus.coding.grpc.TestCaseResultDto;
import com.codecampus.coding.helper.CodingHelper;
import com.codecampus.coding.repository.CodeSubmissionRepository;
import com.codecampus.coding.repository.CodeSubmissionResultRepository;
import com.codecampus.submission.grpc.CodeSubmissionDto;
import com.codecampus.submission.grpc.CreateCodeSubmissionRequest;
import com.codecampus.submission.grpc.SubmissionSyncServiceGrpc;
import com.codecampus.submission.grpc.TestCaseResultSyncDto;
import com.google.protobuf.Timestamp;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodeJudgeService {

  static final Path RUNNER_ROOT =
      Path.of(System.getenv().getOrDefault("RUNNER_ROOT", "/work"));

  CodeSubmissionRepository codeSubmissionRepository;
  CodeSubmissionResultRepository codeSubmissionResultRepository;
  CodingHelper codingHelper;
  DockerSandboxService dockerSandboxService;
  SubmissionSyncServiceGrpc.SubmissionSyncServiceBlockingStub
      submissionStub;

  private static Path createWorkDir() throws IOException {
    // Đảm bảo thư mục RUNNER_ROOT tồn tại và có quyền
    if (!Files.exists(RUNNER_ROOT)) {
      Files.createDirectories(RUNNER_ROOT);
      Files.setPosixFilePermissions(
          RUNNER_ROOT, PosixFilePermissions.fromString("rwxrwxrwx"));
    }
    Path workDir = Files.createTempDirectory(RUNNER_ROOT, "pg_");
    Files.setPosixFilePermissions(
        workDir, PosixFilePermissions.fromString(
            "rwxrwxrwx"));
    return workDir;
  }

  @Transactional
  public SubmitCodeResponse judgeCodeSubmission(
      SubmitCodeRequest request) {

    CodingExercise codingExercise = codingHelper
        .findCodingOrThrow(request.getExerciseId());

    CodeSubmission codeSubmission = CodeSubmission.builder()
        .exercise(codingExercise)
        .studentId(request.getStudentId())
        .language(request.getLanguage())
        .sourceCode(request.getSourceCode())
        .submittedAt(Instant.now())
        .timeTakenSeconds(request.getTimeTakenSeconds())
        .build();
    codeSubmissionRepository.saveAndFlush(codeSubmission);

    final int memoryMbLimit = codingExercise.getMemoryLimit() > 0 ?
        codingExercise.getMemoryLimit() : 256;
    final float cpusLimit = 0.5f;
    final int compileMemoryMb = Math.max(memoryMbLimit, 512);

    int passedCount = 0;
    int peakMemoryMb = 0;
    List<TestCaseResultSyncDto> testCaseResultSyncDtoList =
        new ArrayList<>();

    /* Compile một lần, chạy từng test-case */
    Path workDir = null;
    CompiledArtifact bin;
    try {
      workDir = createWorkDir();
      bin = dockerSandboxService.compile(
          request.getLanguage(),
          request.getSourceCode(),
          workDir,
          compileMemoryMb,
          cpusLimit);

      for (TestCase testCase : codingExercise.getTestCases()) {
        CodeResult codeResult = dockerSandboxService.runTest(
            bin,
            testCase,
            memoryMbLimit,
            cpusLimit);

        if (codeResult.isPassed()) {
          passedCount++;
        }
        peakMemoryMb = Math.max(peakMemoryMb, codeResult.getMemoryMb());

        CodeSubmissionResult codeSubmissionResult =
            CodeSubmissionResult.builder()
                .submission(codeSubmission)
                .testCase(testCase)
                .passed(codeResult.isPassed())
                .runtimeMs(codeResult.getRuntimeMs())
                .memoryMb(codeResult.getMemoryMb())
                .output(codeResult.getOutput())
                .errorMessage(codeResult.getError())
                .build();
        codeSubmissionResultRepository.save(codeSubmissionResult);

        // Sync
        testCaseResultSyncDtoList.add(TestCaseResultSyncDto.newBuilder()
            .setTestCaseId(testCase.getId())
            .setPassed(codeResult.isPassed())
            .setRuntimeMs(codeResult.getRuntimeMs())
            .setMemoryMb(codeResult.getMemoryMb())
            .setOutput(
                codeResult.getOutput() == null ? "" :
                    codeResult.getOutput())
            .setErrorMessage(
                codeResult.getError() == null ? "" :
                    codeResult.getError())
            .build());
      }
    } catch (InterruptedException | IOException e) {
      throw new RuntimeException(e);
    } finally {
      safeDeleteDir(workDir);
    }

    codeSubmission.setScore(passedCount);
    codeSubmission.setTotalPoints(codingExercise.getTestCases().size());
    boolean allPassed = (passedCount == codeSubmission.getTotalPoints());
    codeSubmission.setPassed(allPassed);


    /* Sync tới submission service */
    submissionStub.createCodeSubmission(
        CreateCodeSubmissionRequest.newBuilder()
            .setSubmission(CodeSubmissionDto.newBuilder()
                .setId(codeSubmission.getId())
                .setExerciseId(request.getExerciseId())
                .setStudentId(request.getStudentId())
                .setScore(passedCount)
                .setTotalPoints(codeSubmission.getTotalPoints())
                .setLanguage(request.getLanguage())
                .setSourceCode(request.getSourceCode())
                .setSubmittedAt(Timestamp.newBuilder()
                    .setSeconds(
                        Instant.now().getEpochSecond())
                    .setNanos(Instant.now().getNano())
                    .build())
                .setTimeTakenSeconds(
                    request.getTimeTakenSeconds())
                .addAllResults(testCaseResultSyncDtoList)
                .setPeakMemoryMb(peakMemoryMb)
                .setCpus(cpusLimit)
                .setMemoryMb(memoryMbLimit)
                .build())
            .build());


    return SubmitCodeResponse.newBuilder()
        .setSubmissionId(codeSubmission.getId())
        .setScore(passedCount)
        .setTotalPoints(codeSubmission.getTotalPoints())
        .setPassed(allPassed)
        .addAllResults(testCaseResultSyncDtoList.stream()
            .map(r -> TestCaseResultDto.newBuilder()
                .setTestCaseId(r.getTestCaseId())
                .setPassed(r.getPassed())
                .setRuntimeMs(r.getRuntimeMs())
                .setMemoryMb(r.getMemoryMb())
                .setOutput(r.getOutput())
                .setErrorMessage(r.getErrorMessage())
                .build())
            .toList())
        .setMemoryMb(memoryMbLimit)
        .setCpus(cpusLimit)
        .setPeakMemoryMb(peakMemoryMb)
        .build();
  }

  void safeDeleteDir(Path dir) {
    if (dir == null) {
      return;
    }
    try (Stream<Path> s = Files.walk(dir)) {
      s.sorted(Comparator.reverseOrder()).forEach(p -> {
        try {
          Files.deleteIfExists(p);
        } catch (Exception ignored) {
        }
      });
    } catch (Exception ignored) {
    }
  }
}

