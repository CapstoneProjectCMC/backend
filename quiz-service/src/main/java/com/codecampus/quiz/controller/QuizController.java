package com.codecampus.quiz.controller;

import com.codecampus.quiz.dto.common.ApiResponse;
import com.codecampus.quiz.grpc.LoadQuizResponse;
import com.codecampus.quiz.grpc.QuizExerciseDto;
import com.codecampus.quiz.grpc.SubmitQuizRequest;
import com.codecampus.quiz.grpc.SubmitQuizResponse;
import com.codecampus.quiz.service.QuizService;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizController {

    QuizService quizService;

    @GetMapping("/{quizId}")
    ApiResponse<QuizExerciseDto> getQuiz(
            @PathVariable String quizId) {
        return ApiResponse.<QuizExerciseDto>builder()
                .result(quizService.getQuizExerciseDto(quizId))
                .message("Lấy quiz thành công!")
                .build();
    }

    @GetMapping("/{quizId}/load")
    ApiResponse<LoadQuizResponse> loadQuiz(
            @PathVariable String quizId) {

        return ApiResponse.<LoadQuizResponse>builder()
                .result(quizService.loadQuiz(quizId))
                .message("Load quiz thành công!")
                .build();
    }

    //FIXME Hardcode để fix được bug convert từ JSON sang protobuf hứng data
    @PostMapping("/{quizId}/submit")
    ApiResponse<SubmitQuizResponse> submitQuiz(
            @PathVariable String quizId,
            @RequestBody String bodyJson)
            throws InvalidProtocolBufferException {

        /* 1. Parse JSON ⇒ protobuf */
        SubmitQuizRequest.Builder builder = SubmitQuizRequest.newBuilder();
        JsonFormat.parser()
                .ignoringUnknownFields()
                .merge(bodyJson, builder);

        /* 2. Bù exerciseId nếu client không gửi */
        if (builder.getExerciseId().isEmpty()) {
            builder.setExerciseId(quizId);
        }

        /* 3. Kiểm tra khớp path/body */
        Assert.isTrue(quizId.equals(builder.getExerciseId()),
                "exerciseId mismatch");

        /* 4. Chấm điểm & trả kết quả */
        SubmitQuizResponse rsp = quizService.submitQuiz(builder.build());

        return ApiResponse.<SubmitQuizResponse>builder()
                .result(rsp)
                .message("Nộp bài thành công!")
                .build();
    }
}
