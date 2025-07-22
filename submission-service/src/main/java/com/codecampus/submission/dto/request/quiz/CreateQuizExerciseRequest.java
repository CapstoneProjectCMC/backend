package com.codecampus.submission.dto.request.quiz;

import com.codecampus.submission.dto.request.CreateExerciseRequest;

public record CreateQuizExerciseRequest(
        CreateExerciseRequest createExerciseRequest,
        AddQuizDetailRequest addQuizDetailRequest
) {
}
