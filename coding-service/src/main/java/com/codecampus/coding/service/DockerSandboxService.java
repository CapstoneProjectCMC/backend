package com.codecampus.coding.service;

import com.codecampus.coding.dto.response.CodeResult;
import com.codecampus.coding.dto.response.CompiledArtifact;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DockerSandboxService {

    /**
     * Biên dịch mã nguồn 1 lần, trả về “artifact” (đường dẫn | tên bin)
     * để run nhiều test-case.
     */
    public CompiledArtifact compile(
            String language,
            String source,
            Path workDir
    ) throws IOException, InterruptedException {

        // 1. Ghi file nguồn ra thư mục tạm
        switch (language) {
            case "python" ->
                    Files.writeString(workDir.resolve("Main.py"), source);
            case "cpp" ->
                    Files.writeString(workDir.resolve("Main.cpp"), source);
            case "java" ->
                    Files.writeString(workDir.resolve("Main.java"), source);
            default ->
                    throw new IllegalArgumentException("Ngôn ngữ chưa hỗ trợ");
        }

        /* 2. Python không cần build */
        if ("python".equals(language)) {
            // Python không cần build
            return new CompiledArtifact(language, null, workDir);
        }

        // 3. Tên nhị phân / jar random để tránh trùng
        String binName = "bin_" + UUID.randomUUID();
        String image = "capstoneprojectpythondocker";   // đã có sẵn GCC, JDK

        ProcessBuilder processBuilder = switch (language) {
            case "cpp" -> new ProcessBuilder(
                    "docker", "run", "--rm",
                    "-v", workDir + ":/code", image,
                    "g++", "-O2", "-std=c++17", "/code/Main.cpp", "-o",
                    "/code/" + binName);
            case "java" -> new ProcessBuilder(
                    "docker", "run", "--rm",
                    "-v", workDir + ":/code", image,
                    "bash", "-c",
                    "javac /code/Main.java -d /code");
            default -> throw new IllegalStateException();
        };

        exec(processBuilder);
        return new CompiledArtifact(language,
                "java".equals(language) ? "Main" : binName, workDir);
    }

    /**
     * Chạy 1 test-case.
     *
     * @return CodeResult gồm passed / runtime / output / error …
     */
    public CodeResult runTest(
            CompiledArtifact compiledArtifact,
            TestCase testCase,
            int memoryMb,
            float cpus) {
        String container = "judge_" + UUID.randomUUID();

        try {
            /* 1. khởi tạo container rỗng, giới hạn RAM + CPU, network none */
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "docker", "run", "-dit",
                    "--memory=%sm".formatted(memoryMb),
                    "--cpus=" + cpus,
                    "--network", "none",
                    "-v", compiledArtifact.workDir().toString() + ":/app",
                    "--name", container,
                    "capstoneprojectpythondocker", "bash");
            exec(processBuilder);

            /* Build */
            List<String> cmd = switch (compiledArtifact.lang()) {
                case "python" -> List.of("python3", "/app/Main.py");
                case "cpp" -> List.of("/app/" + compiledArtifact.binary());
                case "java" -> List.of("java", "-cp", "/app",
                        compiledArtifact.binary());
                default -> throw new IllegalStateException();
            };

            /* Chạy */
            long startTime = System.nanoTime();
            Process run = new ProcessBuilder(
                    Stream.concat(Stream.of("docker", "exec", "-i", container),
                                    cmd.stream())
                            .toList())
                    .start();

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(run.getOutputStream()))) {
                writer.write(testCase.getInput());
            }

            String out = read(run.getInputStream());
            String err = read(run.getErrorStream());
            int exitCode = run.waitFor();
            long endTime = System.nanoTime();

            boolean passed =
                    exitCode == 0 && equal(out, testCase.getExpectedOutput());

            return new CodeResult(
                    passed,
                    (int) ((endTime - startTime) / 1_000_000),
                    0, out.trim(), err.trim());

        } catch (Exception ex) {
            log.error("Sandbox error", ex);
            return new CodeResult(false, 0, 0, "", ex.getMessage());
        } finally {
            silent("docker", "rm", "-f", container);
        }
    }

    void exec(ProcessBuilder processBuilder)
            throws IOException, InterruptedException {
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        String log = new String(process.getInputStream().readAllBytes());
        int code = process.waitFor();
        if (code != 0) {
            throw new RuntimeException("docker run failed: " + log);
        }
    }

    String read(InputStream inputStream)
            throws IOException {
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

