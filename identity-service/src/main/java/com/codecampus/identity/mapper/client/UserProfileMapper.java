package com.codecampus.identity.mapper.client;

import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.profile.UserProfileCreationRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    @Mapping(target = "userId", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = {
            "username",
            "email",
            "password"
    })
    UserProfileCreationRequest toUserProfileCreationRequestFromUserCreationRequest(
            UserCreationRequest userCreationRequest);
}

