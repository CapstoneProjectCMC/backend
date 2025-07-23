package com.codecampus.coding.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SubmissionRequestDto {
  String submissionId;
  String submitedCode;
  String userId;
  String exerciseId;
  int memory;
  float cpus;
}
