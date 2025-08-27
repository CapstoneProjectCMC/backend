package com.codecampus.profile.repository.client;

import com.codecampus.profile.dto.common.ApiResponse;
import dtos.ContestStatusDto;
import dtos.ContestSummary;
import dtos.ExerciseStatusDto;
import dtos.ExerciseSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "submission-service",
    url = "${app.services.submission}",
    path = "/internal"
)
public interface SubmissionClient {
  @GetMapping("/exercise/{exerciseId}/summary")
  ApiResponse<ExerciseSummary> internalGetExerciseSummary(
      @PathVariable("exerciseId") String exerciseId);

  @GetMapping("/exercise/{exerciseId}/status")
  ApiResponse<ExerciseStatusDto> internalGetExerciseStatus(
      @PathVariable String exerciseId,
      @RequestParam("studentId") String studentId);

  @GetMapping("/contest/{contestId}/summary")
  ApiResponse<ContestSummary> internalGetContestSummary(
      @PathVariable String contestId);

  @GetMapping("/contest/{contestId}/status")
  ApiResponse<ContestStatusDto> internalGetContestStatus(
      @PathVariable String contestId,
      @RequestParam("studentId") String studentId);
}
