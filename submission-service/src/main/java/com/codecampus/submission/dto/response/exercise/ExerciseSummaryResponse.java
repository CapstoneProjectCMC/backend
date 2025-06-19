package com.codecampus.submission.dto.response.exercise;

import java.math.BigDecimal;
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
public class ExerciseSummaryResponse {
  String id;
  String userId;
  String title;
  String description;
  int difficulty;
  String orgId;
  boolean visibility;
  boolean active;
  BigDecimal cost;
  Boolean freeForOrg;
  Instant startTime;
  Instant endTime;
  int duration;
  String allowDiscussionId;
  Set<String> resourceIds;
}
