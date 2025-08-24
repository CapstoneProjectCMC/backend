package com.codecampus.post.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostAccessResponseDto {
  private String postId;
  private String userId;
  private boolean isExcluded;
}
