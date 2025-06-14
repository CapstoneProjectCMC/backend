package com.codecampus.identity.mapper.mapper;

import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.profile.UserProfileCreationRequest;
import com.codecampus.identity.utils.ConvertUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileMapper
{
  UserProfileCreationRequest toUserProfileCreationRequest(
      UserCreationRequest userCreationRequest);
}
