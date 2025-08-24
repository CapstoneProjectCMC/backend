package com.codecampus.coding.dto.data;

import lombok.Data;

@Data
public class PlaygroundRunPayload {
  private String language;      // "python" | "cpp" | "java"
  private String sourceCode;
  private String stdin;
  private Integer memoryMb;
  private Float cpus;
  private Integer timeLimitSec;
}