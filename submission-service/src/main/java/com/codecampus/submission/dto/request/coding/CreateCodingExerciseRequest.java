package com.codecampus.submission.dto.request.coding;

import com.codecampus.submission.dto.request.CreateExerciseRequest;
import jakarta.validation.Valid;

public record CreateCodingExerciseRequest(
        @Valid CreateExerciseRequest exercise,
        @Valid AddCodingDetailRequest coding
) {
}
