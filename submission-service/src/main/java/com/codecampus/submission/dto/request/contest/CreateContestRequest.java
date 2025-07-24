package com.codecampus.submission.dto.request.contest;

import com.codecampus.submission.entity.data.AntiCheatConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Set;

public record CreateContestRequest(
        @NotBlank String title,
        String description,
        @NotNull Instant startTime,
        @NotNull Instant endTime,
        boolean rankPublic,
        Instant rankRevealTime,
        AntiCheatConfig antiCheatConfig,
        @NotEmpty Set<String> exerciseIds,
        @NotEmpty Set<String> studentIds   // danh sách HS tham gia
) {
}
