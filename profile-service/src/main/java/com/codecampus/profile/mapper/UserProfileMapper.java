package com.codecampus.profile.mapper;

import com.codecampus.profile.dto.request.UserProfileCreationRequest;
import com.codecampus.profile.dto.request.UserProfileUpdateRequest;
import com.codecampus.profile.dto.response.UserProfileResponse;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.utils.ConvertUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    uses = {ConvertUtils.class}
)
public interface UserProfileMapper {
  @Mapping(
      source = "dob",
      target = "dob",
      qualifiedByName = "DdMmYyyyToInstant"
  )
  UserProfile toUserProfile(UserProfileCreationRequest request);

  @Mapping(
      source = "dob",
      target = "dob",
      qualifiedByName = "instantToDdMmYyyyUTC"
  )
  UserProfileResponse toUserProfileResponse(UserProfile userProfile);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(
      source = "dob",
      target = "dob",
      qualifiedByName = "DdMmYyyyToInstant"
  )
  void updateUserProfile(
      @MappingTarget UserProfile userProfile,
      UserProfileUpdateRequest request
  );
}
