package com.codecampus.payment.mapper;

import com.codecampus.payment.dto.response.UserProfileResponse;
import dtos.UserSummary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSummaryMapper {

  default UserSummary toUserSummaryFromUserProfileResponse(
      UserProfileResponse p) {
    if (p == null) {
      return null;
    }
    return UserSummary.builder()
        .userId(p.getUserId())
        .username(p.getUsername())
        .email(p.getEmail())
        .displayName(p.getDisplayName())
        .avatarUrl(p.getAvatarUrl())
        .active(p.getActive())
        .roles(p.getRoles())
        .build();
  }
}
