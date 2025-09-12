package com.codecampus.post.repository.projection;

public interface UserPostLeaderboardRow {
  String getUserId();

  Long getPostCount();

  Long getCommentCount();

  Long getReactionCount();
}
