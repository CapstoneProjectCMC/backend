package com.codecampus.submission.dto.request;

import com.codecampus.submission.constant.submission.Difficulty;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record UpdateExerciseRequest(
    String title,
    String description,
    Difficulty difficulty,
    BigDecimal cost,
    Boolean freeForOrg,
    Instant startTime,
    Instant endTime,
    Integer duration,
    String allowDiscussionId,
    Set<String> resourceIds,
    Set<String> tags,
    Boolean allowAiQuestion,
    Boolean visibility) {
}
