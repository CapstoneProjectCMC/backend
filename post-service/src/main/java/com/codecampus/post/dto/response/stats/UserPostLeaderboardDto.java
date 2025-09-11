package com.codecampus.post.dto.response.stats;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPostLeaderboardDto {
  String userId;
  long postCount;
  long commentCount;   // tổng comment user này đã viết (optional)
  long reactionCount;  // tổng reaction user này đã tạo (optional)
}