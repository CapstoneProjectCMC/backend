package com.codecampus.organization.mapper;


import com.codecampus.organization.dto.response.UserProfileResponse;
import dtos.UserSummary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSummaryMapper {

  default UserSummary toUserSummaryFromUserProfileResponse(
      UserProfileResponse u) {
    if (u == null) {
      return null;
    }
    return UserSummary.builder()
        .userId(u.userId())
        .username(u.username())
        .email(u.email())
        .displayName(u.displayName() != null ? u.displayName() : "")
        .avatarUrl(u.avatarUrl() != null ? u.avatarUrl() : "")
        .active(u.active() != null ? u.active() : Boolean.TRUE)
        .roles(u.roles())
        .build();
  }
}