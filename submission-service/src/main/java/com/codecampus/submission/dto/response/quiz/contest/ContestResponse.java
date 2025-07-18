package com.codecampus.submission.dto.response.quiz.contest;

import com.codecampus.submission.entity.data.AntiCheatConfig;

import java.time.Instant;

public record ContestResponse(
        String id,
        String title,
        String description,
        Instant startTime,
        Instant endTime,
        boolean rankPublic,
        Instant rankRevealTime,
        int totalQuestions,
        int totalDurationMinutes,
        AntiCheatConfig antiCheatConfig
) {
}