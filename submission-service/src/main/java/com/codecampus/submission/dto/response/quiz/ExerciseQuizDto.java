package com.codecampus.submission.dto.response.quiz;

import com.codecampus.submission.constant.submission.ExerciseType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Set;

@Builder
public record ExerciseQuizDto(
        String id,
        String title,
        String description,
        ExerciseType exerciseType,
        String orgId,
        BigDecimal cost,
        boolean freeForOrg,
        Set<String> tags,

        QuizDetailSliceDto quizDetail) {
}