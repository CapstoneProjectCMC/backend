package com.codecampus.ai.repository;

import com.codecampus.ai.dto.common.ApiResponse;
import com.codecampus.ai.dto.request.exercise.AddQuizDetailRequest;
import com.codecampus.ai.dto.request.exercise.CreateExerciseRequest;
import com.codecampus.ai.dto.request.exercise.CreateQuizExerciseRequest;
import com.codecampus.ai.dto.request.exercise.OptionDto;
import com.codecampus.ai.dto.request.exercise.QuestionDto;
import com.codecampus.ai.dto.response.ExerciseResponse;
import com.codecampus.ai.dto.response.OptionResponse;
import com.codecampus.ai.dto.response.QuestionResponse;
import com.codecampus.ai.dto.response.QuizDetailResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "submission-client",
        url = "${app.services.submission}",
        path = "/internal"
)
public interface SubmissionClient {

//    @PostMapping(
//            value = "exercise",
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    ApiResponse<ExerciseResponse> internalCreateExercise(
//            @RequestBody CreateExerciseRequest request);

    @PostMapping(
            value = "/exercise/quiz",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ApiResponse<ExerciseResponse> internalCreateQuizExercise(
            @RequestBody @Valid CreateQuizExerciseRequest request);

//    @PostMapping(
//            value = "/quiz/exercise/{exerciseId}/quiz-detail",
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    ApiResponse<QuizDetailResponse> internalAddQuizDetail(
//            @PathVariable("exerciseId") String exerciseId,
//            @RequestBody @Valid AddQuizDetailRequest addQuizRequest);

    @PostMapping(
            value = "/quiz/{exerciseId}/question",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ApiResponse<QuestionResponse> internalAddQuestion(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid QuestionDto questionDto)
            throws BadRequestException;

//    @PostMapping(
//            value = "/quiz/question/{questionId}/option",
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    ApiResponse<OptionResponse> internalAddOption(
//            @PathVariable String questionId,
//            @RequestBody @Valid OptionDto request);
}
