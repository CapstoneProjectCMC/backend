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
    //folder chứa code để mount vào docker
    String folder = "/tmp/12345";
    String fileName = submission.getUserId() + ".py";
    String filePath = folder + "/" + fileName;

    // Kiểm tra xem exercise có tồn tại không và lấy thông tin test cases
    Optional<CodingExercise> codingExercise = codingExerciseRepository.findById(submission.getExerciseId());
    if (codingExercise.isEmpty()) {
      return new SubmissionResponseDto(
              submission.getSubmissionId(),
              "Error",
              "",
              "",
              "Exercise not found",
              0,
              0
      );
    }

    try {
      // Lưu code vào folder đã chọn để mount vào Docker
      Files.createDirectories(Paths.get(folder));
      Files.write(Paths.get(filePath), submission.getSubmitedCode().getBytes());

      //so sánh tc
      for (TestCase testCase : codingExercise.get().getTestCases()) {
        String input = testCase.getInput();
        String expected = testCase.getExpectedOutput();

        long startTime = System.nanoTime();

        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "--rm",
                "--memory=" + submission.getMemory() + "m",
                "--cpus=" + submission.getCpus(),
                "-v", folder + ":/app",
                "capstoneproject",
                "python3", "/app/" + fileName
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Ghi input vào process
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
          writer.write(input);
          writer.flush();
        }

        // Đọc output từ process
        String actualOutput;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
          actualOutput = reader.lines().reduce("", (acc, line) -> acc + line + "\n");
        }

        int exitCode = process.waitFor();
        long endTime = System.nanoTime();
        long execTimeMs = (endTime - startTime) / 1_000_000;

        // Kiểm tra output
        boolean correct = isCorrectOutput(actualOutput, expected);
        if (!correct || exitCode != 0) {
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

      return new SubmissionResponseDto(
              submission.getSubmissionId(),
              "Accepted",
              "All test cases passed.",
              "",
              "",
              0,
              0
      );
    } catch (Exception e) {
      return new SubmissionResponseDto(
              submission.getSubmissionId(),
              "Error",
              "",
              "",
              e.getMessage(),
              0,
              0
      );
    }
  }

  public boolean isCorrectOutput(String actualOutput, String expectedOutput) {
    if (actualOutput == null || expectedOutput == null) return false;

    String normActual = normalize(actualOutput);
    String normExpected = normalize(expectedOutput);

    return normActual.equals(normExpected);
  }

    /**
     * Hàm normalize để chuẩn hóa chuỗi đầu vào.
     * Loại bỏ khoảng trắng thừa, chuyển đổi xuống dòng và loại bỏ các ký tự không cần thiết.
     *
     * @param s Chuỗi đầu vào cần chuẩn hóa
     * @return Chuỗi đã được chuẩn hóa
     */
  private String normalize(String s) {
    return s.trim()
            .replace("\r\n", "\n")
            .replaceAll("[ \t]+", " ")
            .replaceAll("[ \t]*\n[ \t]*", "\n");
  }
}
