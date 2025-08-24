package com.codecampus.search.mapper;

import com.codecampus.search.dto.response.UserProfileResponse;
import dtos.UserSummary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSummaryMapper {
  default UserSummary toUserSummaryFromUserProfileResponse(
      UserProfileResponse userProfileResponse) {
    if (userProfileResponse == null) {
      return null;
    }
    return UserSummary.builder()
        .userId(userProfileResponse.getUserId())
        .username(userProfileResponse.getUsername())
        .email(userProfileResponse.getEmail())
        .displayName(userProfileResponse.getDisplayName())
        .avatarUrl(userProfileResponse.getAvatarUrl())
        .active(userProfileResponse.isActive())
        .roles(userProfileResponse.getRoles())
        .build();
  }
}