package com.codecampus.post.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAccessRequestDto {
  private String postId;
  private List<String> userIds;
  private Boolean isExcluded;
}
