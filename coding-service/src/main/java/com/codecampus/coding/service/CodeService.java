package com.codecampus.coding.service;

import com.codecampus.coding.dto.request.SubmissionRequestDto;
import com.codecampus.coding.dto.response.SubmissionResponseDto;
import com.codecampus.coding.dto.response.SubmissionTestCaseResultDto;
import com.codecampus.coding.entity.CodingExercise;
import com.codecampus.coding.entity.TestCase;
import com.codecampus.coding.repository.CodingExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CodeService {
  @Autowired
  private CodingExerciseRepository codingExerciseRepository;

  public SubmissionResponseDto compileCode(SubmissionRequestDto submission) throws IOException, InterruptedException {
    String folder = "C:/tmp/12345";
//    String fileName = STR."\{submission.getUserId()}_\{submission.getExerciseId()}.py";
    String fileName = "test.py";
    String filePath = STR."\{folder}/\{fileName}";
    String containerName = STR."judge_\{submission.getSubmissionId()}";

    Optional<CodingExercise> codingExerciseOpt = codingExerciseRepository.findById(submission.getExerciseId());
    if (codingExerciseOpt.isEmpty()) {
      return SubmissionResponseDto.builder()
              .submissionId(submission.getSubmissionId())
              .status("Error")
              .message("Coding exercise not found")
              .testCases(new ArrayList<>())
              .build();
    }

    CodingExercise codingExercise = codingExerciseOpt.get();

//    // Tạo thư mục và ghi file Python
//    Files.createDirectories(Paths.get(folder));
//    Files.writeString(Paths.get(filePath), submission.getSubmittedCode());

    // Tạo container
    ProcessBuilder startContainer = new ProcessBuilder(
            "docker", "run", "-dit",
            "--memory=" + submission.getMemory() + "m",
            "--cpus=" + submission.getCpus(),
            "--name", containerName,
            "-v", folder + ":/app",
            "--network", "none",
            "capstoneproject",
            "bash"
    );
    Process containerStart = startContainer.start();
    containerStart.waitFor();

    List<SubmissionTestCaseResultDto> results = new ArrayList<>();
    boolean hasWrongAnswer = false;

    try {
      for (TestCase testCase : codingExercise.getTestCases()) {
        String input = testCase.getInput();
        String expected = testCase.getExpectedOutput();

        long startTime = System.nanoTime();

        ProcessBuilder execPython = new ProcessBuilder(
                "docker", "exec", "-i",
                containerName,
                "python3", "/app/" + fileName
        );
        execPython.redirectErrorStream(true);
        Process execProcess = execPython.start();

        // Gửi input vào stdin
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(execProcess.getOutputStream()))) {
          writer.write(input);
          writer.flush();
        }

        // Đọc output
        StringBuilder outputBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(execProcess.getInputStream()))) {
          String line;
          while ((line = reader.readLine()) != null) {
            outputBuilder.append(line).append("\n");
          }
        }

        int exitCode = execProcess.waitFor();
        long endTime = System.nanoTime();
        long execTimeMs = (endTime - startTime) / 1_000_000;

        String actualOutput = outputBuilder.toString().trim();

        boolean correct = isCorrectOutput(actualOutput, expected) && exitCode == 0;
        if (!correct) hasWrongAnswer = true;

        results.add(new SubmissionTestCaseResultDto(
                input,
                expected,
                actualOutput,
                correct,
                execTimeMs
        ));
      }

      String overallStatus = hasWrongAnswer ? "Wrong Answer" : "Accepted";
      String overallMessage = hasWrongAnswer ? "Some test cases failed." : "All test cases passed.";

      return new SubmissionResponseDto(
              submission.getSubmissionId(),
              overallStatus,
              overallMessage,
              results
      );

    } finally {
      // Cleanup container dù có lỗi
      destroyContainer(containerName);
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

