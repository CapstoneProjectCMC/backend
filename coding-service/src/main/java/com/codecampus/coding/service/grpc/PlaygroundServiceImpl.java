package com.codecampus.coding.service.grpc;

import com.codecampus.coding.grpc.playground.PlaygroundServiceGrpc;
import com.codecampus.coding.grpc.playground.RunRequest;
import com.codecampus.coding.grpc.playground.RunUpdate;
import com.google.protobuf.Timestamp;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class PlaygroundServiceImpl
        extends PlaygroundServiceGrpc.PlaygroundServiceImplBase {

    private static final String SANDBOX_IMAGE =
            System.getenv().getOrDefault("SANDBOX_IMAGE",
                    "capstoneprojectpythondocker:latest");
    private static final Path RUNNER_ROOT =
            Path.of(System.getenv().getOrDefault("RUNNER_ROOT", "/work"));
    private final ExecutorService ioPool = Executors.newCachedThreadPool();

    // Huỷ job gần nhất
    private final AtomicReference<Context.CancellableContext> lastJob =
            new AtomicReference<>();

    private static Path createWorkDir() throws IOException {
        Files.createDirectories(RUNNER_ROOT);
        return Files.createTempDirectory(RUNNER_ROOT,
                "pg_"); // <— nằm trong volume host
    }

    @PreDestroy
    public void shutdown() {
        ioPool.shutdownNow();
        try {
            ioPool.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
        ioPool.shutdownNow();
    }

    @Override
    public void run(
            RunRequest runRequest,
            StreamObserver<RunUpdate> streamObserver) {

        // Tạo cancellable context để các pipe I/O biết khi nào dừng.
        final Context.CancellableContext cancellableContext =
                Context.current().withCancellation();

        Path workDir = null;
        String container;

        try {
            emit(streamObserver,
                    RunUpdate.Phase.STARTED,
                    "start",
                    -1, 0, 0
            );

            // 1) Setup workdir & write source
            workDir = createWorkDir();
            String lang = runRequest.getLanguage();

            switch (lang) {
                case "python" -> Files.writeString(workDir.resolve("Main.py"),
                        runRequest.getSourceCode());
                case "cpp" -> Files.writeString(workDir.resolve("Main.cpp"),
                        runRequest.getSourceCode());
                case "java" -> Files.writeString(workDir.resolve("Main.java"),
                        runRequest.getSourceCode());
                default -> {
                    emit(streamObserver,
                            RunUpdate.Phase.ERROR,
                            "Unsupported language",
                            2, 0, 0
                    );
                    streamObserver.onCompleted();
                    return;
                }
            }

            // 2) Compile (python bỏ qua)
            final String image = SANDBOX_IMAGE;
            String binName = "bin_" + UUID.randomUUID();

            if (!"python".equals(lang)) {
                emit(streamObserver,
                        RunUpdate.Phase.COMPILING,
                        "compiling...",
                        -1, 0, 0
                );

                ProcessBuilder compilePb = switch (lang) {
                    case "cpp" ->
                            new ProcessBuilder("docker", "run", "--rm", "-v",
                                    workDir + ":/code", image,
                                    "g++", "-O2", "-std=c++17",
                                    "/code/Main.cpp", "-o", "/code/" + binName);
                    case "java" ->
                            new ProcessBuilder("docker", "run", "--rm", "-v",
                                    workDir + ":/code", image,
                                    "bash", "-c",
                                    "javac /code/Main.java -d /code");
                    default -> null;
                };
                int compileCode = execStreamingBytes(
                        compilePb,
                        streamObserver,
                        RunUpdate.Phase.COMPILE_OUT,
                        RunUpdate.Phase.COMPILE_ERR,
                        cancellableContext
                );
                if (compileCode != 0) {
                    emit(streamObserver,
                            RunUpdate.Phase.ERROR,
                            "Compile failed",
                            compileCode, 0, 0);
                    streamObserver.onCompleted();
                    return;
                }
            }

            // 3) Run
            emit(streamObserver,
                    RunUpdate.Phase.RUNNING,
                    "running...",
                    -1, 0, 0
            );

            int memoryMb = runRequest.getMemoryMb() > 0
                    ? runRequest.getMemoryMb() :
                    256;
            float cpus = runRequest.getCpus() > 0
                    ? runRequest.getCpus() :
                    0.5f;

            container = "pg_" + UUID.randomUUID();

            // Chạy tách docker
            ProcessBuilder startPb = new ProcessBuilder("docker", "run", "-dit",
                    "--memory=%sm".formatted(memoryMb),
                    "--cpus=" + cpus,
                    "--network", "none",
                    "-v", workDir + ":/app",
                    "--name", container,
                    image, "bash");
            mustOk(startPb, "Cannot start sandbox");

            List<String> cmd = switch (lang) {
                case "python" -> List.of("python3", "/app/Main.py");
                case "cpp" -> List.of("/app/" + binName);
                case "java" -> List.of("java", "-cp", "/app/", "Main");
                default -> List.of("sh", "-c", "exit 2");
            };

            long startTime = System.nanoTime();
            Process runPb = new ProcessBuilder(
                    Stream.concat(
                            Stream.of("docker", "exec", "-i", container),
                            cmd.stream()).toList()).start();

            // Feed stdin (non-blocking)
            ioPool.submit(() -> {
                try (BufferedWriter bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(runPb.getOutputStream(),
                                StandardCharsets.UTF_8))) {
                    if (!runRequest.getStdin().isBlank()) {
                        bufferedWriter.write(runRequest.getStdin());
                    }
                } catch (IOException ignored) {
                }
            });

            // Stream stdout / stderr in real - time
            Future<?> fOut = pipeBytes(
                    runPb.getInputStream(),
                    streamObserver,
                    RunUpdate.Phase.STDOUT,
                    cancellableContext
            );

            Future<?> fErr = pipeBytes(
                    runPb.getErrorStream(),
                    streamObserver,
                    RunUpdate.Phase.STDERR,
                    cancellableContext
            );

            // Optional: enforce time limit
            int timeLimit = runRequest.getTimeLimitSec() > 0
                    ? runRequest.getTimeLimitSec() :
                    5;
            boolean finished = runPb.waitFor(timeLimit, TimeUnit.SECONDS);
            int exit = finished ? runPb.exitValue() :
                    124; // 124 = timeout convention
            if (!finished) {
                runPb.destroyForcibly();
            }

            // Chờ pipe xả nốt
            try {
                fOut.get(1, TimeUnit.SECONDS);
            } catch (Exception ignored) {
            }
            try {
                fErr.get(1, TimeUnit.SECONDS);
            } catch (Exception ignored) {
            }

            long endTime = System.nanoTime();

            // Đo memory (bytes → KB) trước khi rm
            int memoryKb = -1;
            try {
                memoryKb = readContainerMemKb(container);
            } catch (Exception e) {
                log.warn("Read memory usage failed: {}", e.toString());
            }

            // Cleanup container
            if (container != null) {
                Process rm = new ProcessBuilder("docker", "rm", "-f", container)
                        .start();
                if (!rm.waitFor(3, TimeUnit.SECONDS)) {
                    log.warn("docker rm -f {} timeout", container);
                } else if (rm.exitValue() != 0) {
                    log.warn("docker rm -f {} failed with {}", container,
                            rm.exitValue());
                }
            }

            // FINISHED
            String msg = (exit == 124) ? "Time limit exceeded" : "done";
            emit(streamObserver,
                    RunUpdate.Phase.FINISHED,
                    msg,
                    exit,
                    (int) ((endTime - startTime) / 1_000_000), memoryKb);
            streamObserver.onCompleted();

        } catch (Exception e) {
            log.error("Playground error", e);
            emit(streamObserver,
                    RunUpdate.Phase.ERROR,
                    e.getMessage(),
                    2, 0, 0);
            safeComplete(streamObserver);
        } finally {
            // Cancel signal để pipe dừng
            try {
                cancellableContext.cancel(null);
            } catch (Exception ignored) {
            }

            // Dọn thư mục tạm
            if (workDir != null) {
                Path workDirPath = workDir;
                ioPool.submit(() -> deleteDir(workDirPath));
            }
            lastJob.compareAndSet(cancellableContext, null);
        }
    }

    private void mustOk(
            ProcessBuilder processBuilder,
            String error)
            throws IOException, InterruptedException {
        int code = execDiscard(processBuilder);
        if (code != 0) {
            throw new IllegalStateException(error);
        }
    }

    // Hàm chạy command
    private int execDiscard(
            ProcessBuilder processBuilder)
            throws IOException, InterruptedException {
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        try (InputStream inputStream = process.getInputStream()) {
            inputStream.readAllBytes();
        }
        return process.waitFor();
    }

    private int execStreamingBytes(
            ProcessBuilder processBuilder,
            StreamObserver<RunUpdate> streamObserver,
            RunUpdate.Phase outPhase, RunUpdate.Phase errPhase,
            Context.CancellableContext cancellableContext)
            throws IOException, InterruptedException {

        Process process = processBuilder.start();
        Future<?> f1 = pipeBytes(process.getInputStream(),
                streamObserver,
                outPhase,
                cancellableContext
        );

        Future<?> f2 = pipeBytes(process.getErrorStream(),
                streamObserver,
                errPhase,
                cancellableContext
        );

        int code = process.waitFor();
        try {
            f1.get(1, TimeUnit.SECONDS);
        } catch (Exception ignored) {
        }
        try {
            f2.get(1, TimeUnit.SECONDS);
        } catch (Exception ignored) {
        }
        return code;
    }

    private Future<?> pipeBytes(
            InputStream inputStream,
            StreamObserver<RunUpdate> streamObserver,
            RunUpdate.Phase phase,
            Context.CancellableContext cancellableContext) {
        return ioPool.submit(() -> {
//            ReadLine() có thể “nuốt” dữ liệu cuối nếu không có newline.
//            try (BufferedReader bufferedReader = new BufferedReader(
//                    new InputStreamReader(inputStream))) {
//                String line;
//                while (!cancellableContext.isCancelled() &&
//                        (line = bufferedReader.readLine()) != null) {
//                    emit(streamObserver, phase, line, -1, 0, 0);
//                }
//            } catch (IOException ignored) {
//            }

            try (inputStream) {
                byte[] buffer = new byte[8192];
                int n;
                while (!cancellableContext.isCancelled() &&
                        (n = inputStream.read(buffer)) != -1) {
                    if (n > 0) {
                        String chunk = new String(buffer, 0, n,
                                StandardCharsets.UTF_8);
                        emit(streamObserver, phase, chunk, -1, 0, 0);
                    }
                }
            } catch (IOException ignored) {
            }
        });
    }

    // Hàm ghi vào dto gRPC
    private void emit(
            StreamObserver<RunUpdate> streamObserver,
            RunUpdate.Phase phase,
            String chunk,
            int exit, int runtimeMs, int memoryKb) {
        streamObserver.onNext(RunUpdate.newBuilder()
                .setPhase(phase)
                .setChunk(chunk == null ? "" : chunk)
                .setExitCode(exit)
                .setRuntimeMs(runtimeMs)
                .setMemoryKb(memoryKb)
                .setTs(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .build()
        );
    }

    private int readContainerMemKb(String container)
            throws IOException, InterruptedException {
        Process process = new ProcessBuilder(
                "docker", "exec", container, "bash", "-c",
                "cat /sys/fs/cgroup/memory.current || cat /sys/fs/cgroup/memory/memory.usage_in_bytes").start();
        String output = new String(
                process.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        ).trim();
        process.waitFor(1, TimeUnit.SECONDS);
        long bytes = Long.parseLong(output);
        return (int) (bytes / 1_024);
    }

    private void safeComplete(
            StreamObserver<?> streamObserver) {
        try {
            streamObserver.onCompleted();
        } catch (Exception ignored) {
        }
    }

    private void deleteDir(Path path) {
        try (Stream<Path> pathStream = Files.walk(path)) {
            pathStream.sorted(Comparator.reverseOrder())
                    .forEach(pp -> {
                        try {
                            Files.deleteIfExists(pp);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }
}
