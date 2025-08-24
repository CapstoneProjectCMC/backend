package com.codecampus.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDto {
  private String userId;
  private String username;
  private String email;
  private String role;
  private String avatarUrl;
  private String backgroundUrl;
}
