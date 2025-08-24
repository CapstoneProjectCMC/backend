package com.codecampus.submission.dto.request.coding;

import com.codecampus.submission.dto.request.CreateExerciseRequest;

public record CreateCodingExerciseRequest(
    CreateExerciseRequest createExerciseRequest,
    AddCodingDetailRequest addCodingDetailRequest
) {
}