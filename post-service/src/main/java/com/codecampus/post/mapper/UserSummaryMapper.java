package com.codecampus.post.mapper;

import com.codecampus.post.dto.response.ProfileResponseDto;
import dtos.UserSummary;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSummaryMapper {
  default UserSummary toUserSummaryFromProfileResponseDto(
      ProfileResponseDto profile) {
    if (profile == null) {
      return null;
    }
    return UserSummary.builder()
        .userId(profile.getUserId())
        .username(profile.getUsername())
        .email(profile.getEmail())
        .displayName(profile.getUsername())
        .avatarUrl(profile.getAvatarUrl())
        .active(null)
        .roles(Set.of(
            profile.getRole()))
        .build();
  }
}
