package com.codecampus.submission.dto.response.coding.coding_detail;

import com.codecampus.submission.constant.submission.Difficulty;
import com.codecampus.submission.constant.submission.ExerciseType;
import dtos.UserSummary;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Builder
public record ExerciseCodingDetailResponse(
        String id,
        UserSummary user,
        String title,
        String description,
        ExerciseType exerciseType,
        Difficulty difficulty,
        String orgId,
        boolean active,
        BigDecimal cost,
        boolean freeForOrg,
        Instant startTime,
        Instant endTime,
        int duration,
        String allowDiscussionId,
        Set<String> resourceIds,
        Set<String> tags,
        boolean allowAiQuestion,
        boolean visibility,

        /* ------ phần mới cho coding ------ */
        CodingDetailSliceDetailResponse codingDetail,

        // audit
        String createdBy, Instant createdAt,
        String updatedBy, Instant updatedAt,
        String deletedBy, Instant deletedAt
) {
}