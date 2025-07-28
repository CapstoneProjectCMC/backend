package com.codecampus.ai.repository.httpClient;

import com.codecampus.ai.config.AuthenticationRequestInterceptor;
import com.codecampus.ai.config.FeignConfig;
import com.codecampus.ai.dto.common.ApiResponse;
import com.codecampus.ai.dto.request.exercise.CreateQuizExerciseRequest;
import com.codecampus.ai.dto.request.exercise.QuestionDto;
import com.codecampus.ai.dto.response.ExerciseResponse;
import com.codecampus.ai.dto.response.QuestionResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "submission-client",
        url = "${app.services.submission}",
        path = "/internal",
        configuration = {
                AuthenticationRequestInterceptor.class, FeignConfig.class}
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
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<ExerciseResponse> internalCreateQuizExercise(
            @RequestBody CreateQuizExerciseRequest request);

//    @PostMapping(
//            value = "/quiz/exercise/{exerciseId}/quiz-detail",
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    ApiResponse<QuizDetailResponse> internalAddQuizDetail(
//            @PathVariable("exerciseId") String exerciseId,
//            @RequestBody @Valid AddQuizDetailRequest addQuizRequest);

    @PostMapping(
            value = "/quiz/{exerciseId}/question",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<QuestionResponse> internalAddQuestion(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody QuestionDto questionDto)
            throws BadRequestException;

//    @PostMapping(
//            value = "/quiz/question/{questionId}/option",
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    ApiResponse<OptionResponse> internalAddOption(
//            @PathVariable String questionId,
//            @RequestBody @Valid OptionDto request);
}
