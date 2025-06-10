package com.codecampus.profile.service;

import com.codecampus.profile.dto.request.UserProfileCreationRequest;
import com.codecampus.profile.dto.request.UserProfileUpdateRequest;
import com.codecampus.profile.dto.response.UserProfileResponse;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.exception.AppException;
import com.codecampus.profile.exception.ErrorCode;
import com.codecampus.profile.mapper.UserProfileMapper;
import com.codecampus.profile.repository.UserProfileRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService
{
  UserProfileRepository userProfileRepository;

  UserProfileMapper userProfileMapper;

  public UserProfileResponse createUserProfile(
      UserProfileCreationRequest request) {
    UserProfile userProfile = userProfileMapper.toUserProfile(request);
    userProfile = userProfileRepository.save(userProfile);

    return userProfileMapper.toUserProfileResponse(userProfile);
  }

  public UserProfileResponse getUserProfileByUserId(String userId) {
    UserProfile userProfile = userProfileRepository
        .findByUserId(userId)
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );

    return userProfileMapper.toUserProfileResponse(userProfile);
  }

  public UserProfileResponse getUserProfileById(String id)
  {
    UserProfile userProfile = userProfileRepository
        .findById(id)
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );

    return userProfileMapper.toUserProfileResponse(userProfile);
  }

  @PreAuthorize("hasRole('ADMIN')")
  public List<UserProfileResponse> getAllUserProfiles() {
    var profiles = userProfileRepository.findAll();

    return profiles
        .stream()
        .map(userProfileMapper::toUserProfileResponse)
        .toList();
  }

  public UserProfileResponse getMyUserProfile()
  {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId = authentication.getName();

    var profile = userProfileRepository
        .findByUserId(userId)
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );

    return userProfileMapper.toUserProfileResponse(profile);
  }

  public UserProfileResponse updateMyUserProfile(
      UserProfileUpdateRequest request) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    String userId = authentication.getName();

    var profile = userProfileRepository
        .findByUserId(userId)
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );

    userProfileMapper.updateUserProfile(profile, request);
    return userProfileMapper.toUserProfileResponse(userProfileRepository.save(profile));
  }
}
