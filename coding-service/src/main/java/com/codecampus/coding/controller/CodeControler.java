package com.codecampus.coding.controller;

import com.codecampus.coding.dto.common.ApiResponse;
import com.codecampus.coding.dto.request.SubmissionRequestDto;
import com.codecampus.coding.dto.response.SubmissionResponseDto;
import com.codecampus.coding.service.CodeService;
import com.google.protobuf.Api;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/code")
public class CodeControler
{
  @Autowired
  public CodeService codeService;

  @PostMapping("/compile")
  public ResponseEntity<ApiResponse<?>> compileCode(@RequestBody SubmissionRequestDto requestDto) throws IOException, InterruptedException {
    SubmissionResponseDto powerShellresponse = codeService.compileCode(requestDto);
    return ResponseEntity.ok(ApiResponse.builder().result(powerShellresponse).build());
  }
}
