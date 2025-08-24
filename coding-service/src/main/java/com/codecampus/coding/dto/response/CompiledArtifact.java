package com.codecampus.coding.dto.response;

import jakarta.annotation.Nullable;
import java.nio.file.Path;
import lombok.Builder;

@Builder
public record CompiledArtifact(
    String lang,
    @Nullable String binary,
    Path workDir) {
}
