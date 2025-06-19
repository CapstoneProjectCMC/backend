package com.codecampus.submission.dto.response.contest;

import com.codecampus.submission.dto.response.exercise.ExerciseSummaryResponse;
import java.time.Instant;
import java.util.Set;
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
public class ContestResponse {
  String id;
  String title;
  String description;
  String orgId;
  Instant startTime;
  Instant endTime;
  boolean rankPublic;
  Instant rankRevealTime;
  Set<ExerciseSummaryResponse> exercises;
}
