package com.codecampus.identity.mapper.authentication;

import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserUpdateRequest;
import com.codecampus.identity.dto.response.authentication.UserResponse;
import com.codecampus.identity.entity.account.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
  User toUser(UserCreationRequest userCreationRequest);

  UserResponse toUserResponse(User user);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "roles", ignore = true)
  void updateUser(
      @MappingTarget User user,
      UserUpdateRequest userUpdateRequest
  );
}
