package com.codecampus.coding.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.springframework.stereotype.Service;

@Service
public class CodeService
{
  public String compileCode(int memory, float cpus, String fileName, String hostPath) {
    StringBuilder output = new StringBuilder();
    try {
      ProcessBuilder pb = new ProcessBuilder(
          "docker", "run", "--rm",
          "--memory=" + memory + "m",
          "--cpus=" + cpus,
          "-v", hostPath + ":/app",
          "capstoneproject",
          "python3",
          "/app/" + fileName
      );

      pb.redirectErrorStream(true);  // ghép stderr vào stdout
      Process process = pb.start();

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(process.getInputStream(), "UTF-8")
      );

      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append("\n");
      }

      process.waitFor();

    } catch (Exception e) {
      output.append("Lỗi: ").append(e.getMessage());
    }
    return output.toString();
  }
}
