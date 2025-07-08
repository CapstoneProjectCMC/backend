package com.codecampus.submission.dto.request.quiz;

import com.codecampus.submission.dto.request.CreateExerciseRequest;
import jakarta.validation.Valid;

public record CreateQuizExerciseRequest(
        @Valid CreateExerciseRequest exercise,
        @Valid AddQuizDetailRequest quiz
) {
}
