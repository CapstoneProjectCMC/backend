package com.codecampus.post.repository.projection;

import java.time.Instant;

public interface AdminPostStatRow {
  String getPostId();

  String getTitle();

  String getUserId();

  String getOrgId();

  Instant getCreatedAt();

  Instant getLastActivityAt();

  Long getCommentCount();

  Long getUpvoteCount();

  Long getDownvoteCount();
}