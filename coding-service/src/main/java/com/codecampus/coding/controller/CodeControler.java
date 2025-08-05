package com.codecampus.coding.controller;

import com.codecampus.coding.dto.common.ApiResponse;
import com.codecampus.coding.dto.request.SubmissionRequestDto;
import com.codecampus.coding.dto.response.SubmissionResponseDto;
import com.codecampus.coding.service.DockerSandboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/code")
public class CodeControler {
    @Autowired
    public DockerSandboxService dockerSandboxService;

    @PostMapping("/compile")
    public ResponseEntity<ApiResponse<?>> compileCode(
            @RequestBody SubmissionRequestDto requestDto)
            throws IOException, InterruptedException {
        SubmissionResponseDto powerShellresponse =
                dockerSandboxService.compileCode(requestDto);
        return ResponseEntity.ok(
                ApiResponse.builder().result(powerShellresponse).build());
    }
}
