package com.codecampus.identity.mapper.client;

import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.profile.UserProfileCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper
{
  UserProfileCreationRequest toUserProfileCreationRequest(
      UserCreationRequest userCreationRequest);
}

