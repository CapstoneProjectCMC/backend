package com.codecampus.identity.mapper.authentication;

import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserUpdateRequest;
import com.codecampus.identity.dto.request.profile.UserProfileCreationRequest;
import com.codecampus.identity.dto.response.authentication.UserResponse;
import com.codecampus.identity.entity.account.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User toUserFromUserCreationRequest(UserCreationRequest userCreationRequest);

    @Mapping(target = "userId", ignore = true)
    UserProfileCreationRequest toUserProfileCreationRequestFromUserCreationRequest(
            UserCreationRequest userCreationRequest);

    UserResponse toUserResponseFromUser(
            User user);

    @Mapping(target = "roles", ignore = true)
    void updateUserUpdateRequestToUser(
            @MappingTarget User user,
            UserUpdateRequest userUpdateRequest
    );
}
