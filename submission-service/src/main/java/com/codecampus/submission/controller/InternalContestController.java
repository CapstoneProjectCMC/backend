package com.codecampus.submission.controller;

import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.service.ContestService;
import dtos.ContestStatusDto;
import dtos.ContestSummary;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/internal")
public class InternalContestController {
  ContestService contestService;

  @GetMapping("/contest/{contestId}/summary")
  ApiResponse<ContestSummary> internalGetContestSummary(
      @PathVariable @NotBlank String contestId) {

    return ApiResponse.<ContestSummary>builder()
        .message("Get ra thông tin kỳ thi thành công!")
        .result(contestService.getContestSummary(contestId))
        .build();
  }

  @GetMapping("/contest/{contestId}/status")
  ApiResponse<ContestStatusDto> internalGetContestStatus(
      @PathVariable @NotBlank String contestId,
      @RequestParam("studentId") @NotBlank String studentId) {
    return ApiResponse.<ContestStatusDto>builder()
        .message("Get trạng thái kỳ thi thành công!")
        .result(contestService.getContestStatus(
            contestId,
            studentId,
            Instant.now()))
        .build();
  }
}
