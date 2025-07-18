package com.codecampus.quiz.dto.response;

import com.codecampus.quiz.grpc.QuestionDto;
import com.codecampus.quiz.grpc.QuizExerciseDto;

import java.util.List;

public record LoadQuizResponse(
        QuizExerciseDto exercise,
        List<QuestionDto> questions) {
}
