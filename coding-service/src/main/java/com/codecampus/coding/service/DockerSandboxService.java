package com.codecampus.coding.service;

import com.codecampus.coding.dto.response.CodeResult;
import com.codecampus.coding.dto.response.CompiledArtifact;
import com.codecampus.coding.entity.TestCase;
import com.codecampus.coding.helper.DockerHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DockerSandboxService {

    private static final String SANDBOX_IMAGE =
            System.getenv().getOrDefault("SANDBOX_IMAGE",
                    "capstoneprojectpythondocker:latest");
//    private static final String SANDBOX_IMAGE = "capstoneprojectpythondocker";

    private static final String RUNNER_VOLUME =
            System.getenv().getOrDefault("RUNNER_VOLUME", "runner_data");
    private static final Path RUNNER_ROOT =
            Path.of(System.getenv().getOrDefault("RUNNER_ROOT", "/work"));

    private static Path createWorkDir() throws IOException {
        // Đảm bảo thư mục RUNNER_ROOT tồn tại và có quyền
        if (!Files.exists(RUNNER_ROOT)) {
            Files.createDirectories(RUNNER_ROOT);
            Files.setPosixFilePermissions(
                    RUNNER_ROOT, PosixFilePermissions.fromString("rwxrwxrwx"));
        }
        Path workDir = Files.createTempDirectory(RUNNER_ROOT, "pg_");
        Files.setPosixFilePermissions(
                workDir, PosixFilePermissions.fromString("rwxrwxrwx"));
        return workDir;
    }

    /**
     * Biên dịch mã nguồn 1 lần, trả về “artifact” (đường dẫn | tên bin)
     * để run nhiều test-case.
     */
    public CompiledArtifact compile(
            String language,
            String source,
            Path workDir
    ) throws IOException, InterruptedException {

        // Đảm bảo thư mục workDir có quyền ghi
        Set<PosixFilePermission> perms =
                PosixFilePermissions.fromString("rwxrwxrwx");
        Files.setPosixFilePermissions(workDir, perms);

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
        final boolean inDocker = !DockerHelper.selfContainer().isBlank();
        final String inPath = inDocker
                ? DockerHelper.inVolumePath(workDir)
                : workDir.toString();

        List<String> runBase = inDocker
                ? DockerHelper.cmd("run", "--rm",
                "--volumes-from", DockerHelper.selfContainer(),
                "-w", inPath, SANDBOX_IMAGE)
                : DockerHelper.cmd("run", "--rm",
                "-v", RUNNER_ROOT + ":" + RUNNER_ROOT,
                "-w", inPath, SANDBOX_IMAGE);

        ProcessBuilder compilePb = switch (language) {
            case "cpp" -> new ProcessBuilder(
                    Stream.concat(runBase.stream(),
                                    Stream.of("g++", "-O2", "-std=c++17", "Main.cpp",
                                            "-o", binName))
                            .toList());
            case "java" -> new ProcessBuilder(
                    Stream.concat(runBase.stream(),
                                    Stream.of("javac", "Main.java"))
                            .toList());
            default -> throw new IllegalStateException();
        };

        exec(compilePb);
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
        boolean inDocker = !DockerHelper.selfContainer().isBlank();
        String inPath = inDocker
                ? DockerHelper.inVolumePath(compiledArtifact.workDir())
                : compiledArtifact.workDir().toString();

        try {
            // 1) khởi tạo container rỗng, giới hạn RAM + CPU, network none
            List<String> startArgs = inDocker
                    ? DockerHelper.cmd("run", "-dit",
                    "--memory=%sm".formatted(memoryMb),
                    "--cpus=" + cpus,
                    "--network", "none",
                    "--volumes-from", DockerHelper.selfContainer(),
                    "-w", inPath,
                    "--name", container,
                    SANDBOX_IMAGE, "bash")
                    : DockerHelper.cmd("run", "-dit",
                    "--memory=%sm".formatted(memoryMb),
                    "--cpus=" + cpus,
                    "--network", "none",
                    "-v", inPath + ":" + inPath,
                    "-w", inPath,
                    "--name", container,
                    SANDBOX_IMAGE, "bash");

            exec(new ProcessBuilder(startArgs));

            List<String> cmd = switch (compiledArtifact.lang()) {
                case "python" -> List.of("python3", "Main.py");
                case "cpp" -> List.of("./" + compiledArtifact.binary());
                case "java" -> List.of("java", "Main");
                default -> throw new IllegalStateException();
            };

            // 2) run + feed input
            long startTime = System.nanoTime();
            Process run = new ProcessBuilder(
                    Stream.concat(
                            DockerHelper.cmd("exec", "-i", "-w", inPath,
                                    container).stream(),
                            cmd.stream()).toList())
                    .start();

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(run.getOutputStream()))) {
                writer.write(testCase.getInput());
            }

            String out = read(run.getInputStream());
            String err = read(run.getErrorStream());
            int exitCode = run.waitFor();
            long endTime = System.nanoTime();

            // 3) đo memory trước khi rm
            int memoryKb = 0;
            try {
                memoryKb = readContainerMemKb(container);
            } catch (Exception e) {
                log.warn("Read memory usage failed: {}", e.toString());
            }

            boolean passed =
                    exitCode == 0 && equal(out, testCase.getExpectedOutput());

            return new CodeResult(
                    passed,
                    (int) ((endTime - startTime) / 1_000_000),
                    memoryKb, out.trim(), err.trim());

        } catch (Exception ex) {
            log.error("Sandbox error", ex);
            return new CodeResult(
                    false,
                    0, 0,
                    "", ex.getMessage());
        } finally {
            silent(DockerHelper.cmd("rm", "-f", container)
                    .toArray(new String[0]));
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

    int readContainerMemKb(String container)
            throws IOException, InterruptedException {
        Process process = new ProcessBuilder(
                DockerHelper.cmd("exec", container, "bash", "-c",
                        "cat /sys/fs/cgroup/memory.current || cat /sys/fs/cgroup/memory/memory.usage_in_bytes"))
                .start();

        String output = new String(process.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8).trim();
        process.waitFor();
        long bytes = Long.parseLong(output);
        return (int) (bytes / 1024);
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

