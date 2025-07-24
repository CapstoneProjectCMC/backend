package com.codecampus.coding.service;

import com.codecampus.coding.dto.request.SubmissionRequestDto;
import com.codecampus.coding.dto.response.SubmissionResponseDto;
import com.codecampus.coding.entity.CodingExercise;
import com.codecampus.coding.entity.TestCase;
import com.codecampus.coding.repository.CodingExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class CodeService {
  @Autowired
  private CodingExerciseRepository codingExerciseRepository;

  public SubmissionResponseDto compileCode(SubmissionRequestDto submission) throws IOException, InterruptedException {
    String folder = "/tmp/12345";
    String containerName = "judge_" + submission.getSubmissionId();

    Optional<CodingExercise> codingExercise = codingExerciseRepository.findById(submission.getExerciseId());
    if (codingExercise.isEmpty()) {
      return new SubmissionResponseDto(
              submission.getSubmissionId(),
              "Error", "", "", "Exercise not found", 0, 0
      );
    }

    try {
      // Khởi chạy container
      ProcessBuilder startContainer = new ProcessBuilder(
              "docker", "run", "-dit",
              "--memory=" + submission.getMemory() + "m",
              "--cpus=" + submission.getCpus(),
              "--name", containerName,
              "-v", folder + ":/app",
              "--network", "none", // bảo mật hơn
              "capstoneproject",
              "bash"
      );
      Process containerStart = startContainer.start();
      containerStart.waitFor();

      // Kiểm tra test case
      for (TestCase testCase : codingExercise.get().getTestCases()) {
        String input = testCase.getInput();
        String expected = testCase.getExpectedOutput();

        long startTime = System.nanoTime();

        // Gọi python3 trong container
        ProcessBuilder execPython = new ProcessBuilder(
                "docker", "exec", "-i",
                containerName,
                "python3", "/app/test.py"
        );

        execPython.redirectErrorStream(true);
        Process execProcess = execPython.start();

        // Gửi input vào container
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(execProcess.getOutputStream()))) {
          writer.write(input);
          writer.flush();
        }

        // Đọc output từ container
        String actualOutput;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(execProcess.getInputStream()))) {
          actualOutput = reader.lines().reduce("", (acc, line) -> acc + line + "\n");
        }

        int exitCode = execProcess.waitFor();
        long endTime = System.nanoTime();
        long execTimeMs = (endTime - startTime) / 1_000_000;

        boolean correct = isCorrectOutput(actualOutput, expected);
        if (!correct || exitCode != 0) {
          // Xoá container
          destroyContainer(containerName);

          return new SubmissionResponseDto(
                  submission.getSubmissionId(),
                  "Wrong Answer",
                  actualOutput,
                  expected,
                  "Mismatch in test case with input: " + input,
                  execTimeMs,
                  0
          );
        }
      }

      // Xoá container nếu Accepted
      destroyContainer(containerName);

      return new SubmissionResponseDto(
              submission.getSubmissionId(),
              "Accepted",
              "All test cases passed.",
              "", "", 0, 0
      );

    } catch (Exception e) {
      destroyContainer(containerName);
      return new SubmissionResponseDto(
              submission.getSubmissionId(),
              "Error",
              "", "",
              e.getMessage(),
              0,
              0
      );
    }
  }

  private void destroyContainer(String containerName) throws IOException, InterruptedException {
    new ProcessBuilder("docker", "rm", "-f", containerName).start().waitFor();
  }

  public boolean isCorrectOutput(String actualOutput, String expectedOutput) {
    if (actualOutput == null || expectedOutput == null) return false;

    String normActual = normalize(actualOutput);
    String normExpected = normalize(expectedOutput);

    return normActual.equals(normExpected);
  }

  private String normalize(String s) {
    return s.trim()
            .replace("\r\n", "\n")
            .replaceAll("[ \t]+", " ")
            .replaceAll("[ \t]*\n[ \t]*", "\n");
  }
}

