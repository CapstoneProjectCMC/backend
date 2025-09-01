package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.response.profile.UserProfileResponse;
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
        .userId(userProfileResponse.userId())
        .username(userProfileResponse.username())
        .email(userProfileResponse.email())
        .displayName(userProfileResponse.displayName() != null
            ? userProfileResponse.displayName() : "")
        .avatarUrl(userProfileResponse.avatarUrl() != null ?
            userProfileResponse.avatarUrl() : "")
        .active(userProfileResponse.active() != null ?
            userProfileResponse.active() : Boolean.TRUE)
        .roles(userProfileResponse.roles())
        .build();
  }
}
