package com.codecampus.identity.mapper.kafka;

import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.entity.account.Role;
import com.codecampus.identity.entity.account.User;
import events.user.data.UserPayload;
import events.user.data.UserProfileCreationPayload;
import java.time.Instant;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPayloadMapper {

  UserProfileCreationPayload toUserProfileCreationPayloadFromUserCreationRequest(
      UserCreationRequest userCreationRequest);

  default UserPayload toUserPayloadFromUser(User user) {
    return UserPayload.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .active(user.isEnabled())
        .roles(user.getRoles()
            .stream()
            .map(Role::getName)
            .collect(Collectors.toSet()))
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt() == null ? Instant.now() :
            user.getUpdatedAt())
        .build();
  }
}
