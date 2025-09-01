package com.codecampus.post.mapper;

import com.codecampus.post.dto.response.ProfileResponseDto;
import dtos.UserSummary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSummaryMapper {
  default UserSummary toUserSummaryFromProfileResponseDto(
      ProfileResponseDto profile) {
    if (profile == null) {
      return null;
    }
    return UserSummary.builder()
        .userId(profile.userId())
        .username(profile.username())
        .email(profile.email())
        .displayName(profile.displayName() != null ? profile.displayName() : "")
        .avatarUrl(profile.avatarUrl() != null ? profile.avatarUrl() : "")
        .active(profile.active() != null ? profile.active() : Boolean.TRUE)
        .roles(profile.roles())
        .build();
  }
}
