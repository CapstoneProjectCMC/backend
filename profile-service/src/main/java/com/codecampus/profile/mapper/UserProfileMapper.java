package com.codecampus.profile.mapper;

import com.codecampus.profile.dto.request.UserProfileCreationRequest;
import com.codecampus.profile.dto.request.UserProfileUpdateRequest;
import com.codecampus.profile.dto.response.UserProfileResponse;
import com.codecampus.profile.entity.UserProfile;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserProfileMapper
{
  UserProfile toUserProfile(UserProfileCreationRequest request);

  UserProfileResponse toUserProfileResponse(UserProfile userProfile);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateUserProfile(
      @MappingTarget UserProfile userProfile,
      UserProfileUpdateRequest request
  );
}
