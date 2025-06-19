package com.codecampus.submission.dto.response.exercise;

import com.codecampus.submission.constant.submission.SubmissionStatus;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// Mặc định cho phép response cả null khi Dev
// Khi build thì KHÔNG response null
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmissionResponse {
  String id;
  ExerciseSummaryResponse exercise;
  String userId;
  Instant submittedAt;
  Integer score;
  SubmissionStatus status;
  String language;
  String sourceCode;
  Integer runtime;
  Integer memoryUsed;
}
