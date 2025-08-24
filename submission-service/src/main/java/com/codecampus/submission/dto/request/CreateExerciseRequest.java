package com.codecampus.submission.dto.request;

import com.codecampus.submission.constant.submission.Difficulty;
import com.codecampus.submission.constant.submission.ExerciseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record CreateExerciseRequest(
    @NotBlank String title,
    String description,
    @NotNull Difficulty difficulty,
    @NotNull ExerciseType exerciseType,
    String orgId, // Có trong tổ chức hay không
    @NotNull BigDecimal cost,
    boolean freeForOrg,
    Instant startTime,
    Instant endTime,
    int duration,
    String allowDiscussionId, //Cho thảo luận ở forum nào không
    Set<String> resourceIds, //Thuộc tài liệu nào
    Set<String> tags,
    boolean allowAiQuestion,
    Boolean visibility
) {
}
