package com.codecampus.post.dto.response.stats;


import java.time.Instant;
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
public class AdminPostStatDto {
  String postId;
  String title;
  String userId;
  String orgId;
  Instant createdAt;
  Instant lastActivityAt;
  long commentCount;
  long upvoteCount;
  long downvoteCount;
  long score; // up - down
}