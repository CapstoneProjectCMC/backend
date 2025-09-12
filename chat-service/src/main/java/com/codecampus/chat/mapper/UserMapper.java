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
        .active(
            profilePayload.getActive() != null ? profilePayload.getActive() :
                Boolean.TRUE)
        .roles(profilePayload.getRoles())
        .firstName(profilePayload.getFirstName() != null ?
            profilePayload.getFirstName() : profilePayload.getUsername())
        .lastName(profilePayload.getLastName() != null ?
            profilePayload.getLastName() : "Nguyễn")
        .gender(
            profilePayload.getGender() != null ? profilePayload.getGender() :
                Boolean.TRUE)
        .displayName(profilePayload.getDisplayName() != null ?
            profilePayload.getDisplayName() : profilePayload.getUsername())
        .avatarUrl(profilePayload.getAvatarUrl() != null ?
            profilePayload.getAvatarUrl() : "")
        .backgroundUrl(profilePayload.getBackgroundUrl() != null ?
            profilePayload.getBackgroundUrl() : "")
        .build();
  }
}