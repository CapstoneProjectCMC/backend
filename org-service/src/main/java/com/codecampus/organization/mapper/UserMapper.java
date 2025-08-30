package com.codecampus.organization.mapper;

import com.codecampus.organization.dto.response.UserProfileResponse;
import dtos.UserProfileSummary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserProfileSummary toUserProfileSummaryFromUserProfileResponse(
      UserProfileResponse src);
}