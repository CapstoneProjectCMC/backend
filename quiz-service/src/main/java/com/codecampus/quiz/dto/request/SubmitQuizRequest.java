package com.codecampus.quiz.dto.request;

import com.codecampus.quiz.grpc.AnswerDto;
import java.util.List;

public record SubmitQuizRequest(
    String exerciseId,
    String studentId,
    List<AnswerDto> answers) {
}
