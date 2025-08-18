package com.codecampus.ai.dto.request.coding;

import com.codecampus.ai.dto.request.exercise.CreateExerciseRequest;

public record CreateCodingExerciseRequest(
        CreateExerciseRequest createExerciseRequest,
        AddCodingDetailRequest addCodingDetailRequest
) {
}