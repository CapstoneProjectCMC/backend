package com.codecampus.submission.controller;

import com.codecampus.submission.dto.common.ApiResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class HelloController
{
  @GetMapping("/hello")
  ApiResponse<String> getHello() {
    return ApiResponse.<String>builder()
        .result("Hello World From SUBMISSION SERVICE")
        .message("Hello World From SUBMISSION SERVICE")
        .build();
  }
}
