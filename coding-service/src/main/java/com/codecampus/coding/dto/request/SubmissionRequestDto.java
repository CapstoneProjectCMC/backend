package com.codecampus.coding.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmissionRequestDto {
  String submissionId;
  String submittedCode;
  String userId;
  String exerciseId;
  int memory;
  float cpus;
}
