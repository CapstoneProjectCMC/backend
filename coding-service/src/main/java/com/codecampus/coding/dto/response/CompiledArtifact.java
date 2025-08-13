package com.codecampus.coding.dto.response;

import jakarta.annotation.Nullable;
import lombok.Builder;

import java.nio.file.Path;

@Builder
public record CompiledArtifact(
        String lang,
        @Nullable String binary,
        Path workDir) {
}
