package com.codecampus.coding.helper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper tạo lệnh docker, tự fallback sang “sudo docker …” khi cần.
 */
public final class DockerHelper {
  private static final String SANDBOX_IMAGE =
      System.getenv().getOrDefault("SANDBOX_IMAGE",
          "capstoneprojectpythondocker:latest");
  private static final String RUNNER_VOLUME =
      System.getenv().getOrDefault("RUNNER_VOLUME", "runner_data");
  private static final Path RUNNER_ROOT =
      Path.of(System.getenv().getOrDefault("RUNNER_ROOT", "/work"));
  private static volatile Boolean NEEDS_SUDO = null;

  private DockerHelper() {
  }

  public static List<String> cmd(String... args) {
    var full = new ArrayList<String>();
    if (useSudo()) {
      full.add("sudo");
    }
    full.add("docker");
    full.addAll(Arrays.asList(args));
    return full;
  }

  private static boolean useSudo() {
    if (NEEDS_SUDO != null) {
      return NEEDS_SUDO;
    }

    // Cho phép ép dùng sudo qua biến môi trường (phòng hờ)
    String force = System.getenv("DOCKER_USE_SUDO");
    if (force != null &&
        (force.equals("1") || force.equalsIgnoreCase("true"))) {
      NEEDS_SUDO = true;
      return true;
    }

    try {
      var p = new ProcessBuilder("docker", "system", "info")
          .redirectErrorStream(true)
          .start();

      int code = p.waitFor();
      NEEDS_SUDO = (code != 0);
    } catch (Exception e) {
      NEEDS_SUDO = true;
    }
    return NEEDS_SUDO;
  }

  // PlaygroundServiceImpl & DockerSandboxService
  public static String inVolumePath(Path p) {
    // p = /work/pg_xxx/... (trong coding-service)
    // Cần đường dẫn tương ứng bên trong sandbox: /work/pg_xxx/...
    Path rel = RUNNER_ROOT.relativize(p.toAbsolutePath().normalize());
    return "/work/" + rel.toString().replace("\\", "/");
  }

  public static String selfContainer() {
    return System.getenv().getOrDefault("SELF_CONTAINER", "");
  }
}