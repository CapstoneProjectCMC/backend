package com.codecampus.coding.service;

import com.codecampus.coding.dto.response.CodeResult;
import com.codecampus.coding.entity.TestCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DockerSandboxService {

    public CodeResult run(
            String source,
            String language,
            TestCase testCase,
            int memoryMb,
            float cpus) {
        Path workDir = null;
        String container = STR."judge_\{UUID.randomUUID()}";

        try {
            /* Ghi file */
            workDir = Files.createTempDirectory("judge_");
            Path srcFile = workDir.resolve(language.equals("python")
                    ? "Main.py"
                    : "Main.cpp");

            /* Bắt đầu container */
            Files.writeString(srcFile, source);
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "docker", "run", "-dit",
                    "--memory=" + memoryMb + "m",
                    "--cpus=" + cpus,
                    "-v", workDir + ":/app",
                    "--network", "none",
                    "--name", container,
                    "capstoneprojectpythondocker", "bash");
            exec(processBuilder);

            /* Build */
            if (!language.equals("python")) {
                exec(new ProcessBuilder(
                        "docker", "exec", container,
                        "g++", "-O2", "-std=c++17",
                        "/app/Main.cpp", "-o", "/app/Main"));
            }

            /* Chạy */
            long startTime = System.nanoTime();
            ProcessBuilder run = new ProcessBuilder(
                    "docker", "exec", "-i", container,
                    language.equals("python") ? "python3" : "/app/Main");
            Process proc = run.start();

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(proc.getOutputStream()))) {
                writer.write(testCase.getInput());
                writer.flush();
            }

            String out = read(proc.getInputStream());
            String err = read(proc.getErrorStream());
            int exitCode = proc.waitFor();
            long endTime = System.nanoTime();

            boolean passed =
                    exitCode == 0 && equal(out, testCase.getExpectedOutput());

            return new CodeResult(
                    passed,
                    (int) ((endTime - startTime) / 1_000_000),
                    0, out.trim(), err.trim());

        } catch (Exception e) {
            log.error("Sandbox error", e);
            return new CodeResult(false, 0, 0, "", e.getMessage());
        } finally {
            silent("docker", "rm", "-f", container);
            if (workDir != null) {
                deleteDir(workDir);
            }
        }
    }

    void exec(ProcessBuilder processBuilder)
            throws IOException, InterruptedException {
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        process.waitFor();
    }

    String read(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes());
    }

    boolean equal(String a, String b) {
        return normalize(a).equals(normalize(b));
    }

    void silent(String... cmd) {
        try {
            new ProcessBuilder(cmd).start().waitFor();
        } catch (Exception ignored) {
        }
    }

    void deleteDir(Path p) {
        try {
            Files.walk(p).sorted((x, y) -> -x.compareTo(y)).forEach(t -> {
                try {
                    Files.delete(t);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {
        }
    }

    private String normalize(String s) {
        return s.trim()
                .replace("\r\n", "\n")
                .replaceAll("[ \t]+", " ")
                .replaceAll("[ \t]*\n[ \t]*", "\n");
    }
}

