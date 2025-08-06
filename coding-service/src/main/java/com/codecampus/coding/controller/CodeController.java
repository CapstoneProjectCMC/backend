package com.codecampus.coding.controller;

import com.codecampus.coding.dto.common.ApiResponse;
import com.codecampus.coding.grpc.SubmitCodeRequest;
import com.codecampus.coding.grpc.SubmitCodeResponse;
import com.codecampus.coding.mapper.SubmissionMapper;
import com.codecampus.coding.service.CodeJudgeService;
import com.google.protobuf.util.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodeController {

    CodeJudgeService codeJudgeService;
    SubmissionMapper submissionMapper;

    @PostMapping("/{codingId}/submit")
    public ApiResponse<SubmitCodeResponse> submitCode(
            @PathVariable String codingId,
            @RequestBody String bodyJson)
            throws Exception {

        /* 1. Parse JSON ⇒ protobuf */
        SubmitCodeRequest.Builder builder = SubmitCodeRequest.newBuilder();
        JsonFormat.parser()
                .ignoringUnknownFields()
                .merge(bodyJson, builder);

        /* 2. Bù exerciseId nếu client không gửi */
        if (builder.getExerciseId().isEmpty()) {
            builder.setExerciseId(codingId);
        }

        /* 3. Kiểm tra khớp path/body */
        Assert.isTrue(codingId.equals(builder.getExerciseId()),
                "exerciseId mismatch");

        /* 4. Chấm – đồng bộ về submission-service nằm trong CodeJudgeService */
        SubmitCodeResponse judged = codeJudgeService
                .judgeCodeSubmission(builder.build());

        return ApiResponse.<SubmitCodeResponse>builder()
                .result(judged)
                .build();
    }
}