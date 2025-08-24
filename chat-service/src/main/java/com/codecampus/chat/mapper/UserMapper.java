package com.codecampus.chat.mapper;

import com.codecampus.chat.dto.response.UserProfileResponse;
import dtos.UserProfileSummary;
import events.user.data.UserProfilePayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "roles", ignore = true)
  UserProfileSummary toUserProfileSummaryFromUserProfileResponse(
      UserProfileResponse userProfileResponse);

  default UserProfileSummary toUserProfileSummaryFromUserProfilePayload(
      UserProfilePayload profilePayload) {
    return UserProfileSummary.builder()
        .userId(profilePayload.getUserId())
        .username(profilePayload.getUsername())
        .email(profilePayload.getEmail())
        .active(profilePayload.isActive())
        .roles(profilePayload.getRoles())
        .firstName(profilePayload.getFirstName())
        .lastName(profilePayload.getLastName())
        .gender(profilePayload.getGender())
        .displayName(profilePayload.getDisplayName())
        .avatarUrl(profilePayload.getAvatarUrl())
        .backgroundUrl(profilePayload.getBackgroundUrl())
        .build();
  }
}