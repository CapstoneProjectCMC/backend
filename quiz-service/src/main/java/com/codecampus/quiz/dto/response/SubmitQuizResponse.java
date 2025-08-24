package com.codecampus.quiz.dto.response;

public record SubmitQuizResponse(
    int score,
    int totalPoints,
    boolean passed) {
}
