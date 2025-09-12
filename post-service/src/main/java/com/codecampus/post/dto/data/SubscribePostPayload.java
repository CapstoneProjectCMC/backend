package com.codecampus.post.dto.data;


import java.util.Objects;

public record SubscribePostPayload(String postId) {
  public boolean isValid() {
    return Objects.nonNull(postId) && !postId.isBlank();
  }
}
