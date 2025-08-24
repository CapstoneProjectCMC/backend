package com.codecampus.coding.dto.data;

import com.codecampus.coding.grpc.playground.RunUpdate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaygroundUpdatePayload {
  private String phase;   // STARTED/COMPILING/STDOUT/...
  private String chunk;   // log/stdout/stderr
  private int exitCode;   // set khi FINISHED/ERROR
  private int runtimeMs;  // set khi FINISHED
  private int memoryMb;   // optional
  private long ts;        // epochMillis

  public static PlaygroundUpdatePayload toPlaygroundUpdatePayloadFromRunUpdate(
      RunUpdate runUpdate) {
    long tsMillis = runUpdate.getTs().getSeconds() * 1000L
        + (runUpdate.getTs().getNanos() / 1_000_000L);
    return PlaygroundUpdatePayload.builder()
        .phase(runUpdate.getPhase().name())
        .chunk(runUpdate.getChunk())
        .exitCode(runUpdate.getExitCode())
        .runtimeMs(runUpdate.getRuntimeMs())
        .memoryMb(runUpdate.getMemoryMb())
        .ts(tsMillis)
        .build();
  }
}