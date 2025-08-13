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

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

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
    UserProfile toUserProfileFromUserProfileCreationRequest(
            UserProfileCreationRequest request);

    @Mapping(
            source = "dob",
            target = "dob",
            qualifiedByName = "instantToDdMmYyyyUTC"
    )
    UserProfileResponse toUserProfileResponseFromUserProfile(
            UserProfile userProfile);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(
            source = "dob",
            target = "dob",
            qualifiedByName = "DdMmYyyyToInstant"
    )
    void updateUserProfileUpdateRequestToUserProfile(
            @MappingTarget UserProfile userProfile,
            UserProfileUpdateRequest request
    );
}
