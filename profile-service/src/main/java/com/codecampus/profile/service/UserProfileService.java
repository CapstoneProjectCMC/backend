package com.codecampus.profile.service;

import com.codecampus.profile.dto.request.UserProfileCreationRequest;
import com.codecampus.profile.dto.request.UserProfileUpdateRequest;
import com.codecampus.profile.dto.response.UserProfileResponse;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.exception.AppException;
import com.codecampus.profile.exception.ErrorCode;
import com.codecampus.profile.mapper.UserProfileMapper;
import com.codecampus.profile.repository.UserProfileRepository;
import com.codecampus.profile.utils.SecurityUtils;
import java.time.Instant;
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

    if (userProfileRepository.existsByUserId(request.getUserId())) {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
    }

    UserProfile userProfile = userProfileMapper.toUserProfile(request);
    userProfile.setCreatedAt(Instant.now());
    userProfile = userProfileRepository.save(userProfile);

    return userProfileMapper.toUserProfileResponse(userProfile);
  }

  public UserProfileResponse getUserProfileByUserId(String userId) {
    return userProfileRepository
        .findByUserId(userId)
        .map(userProfileMapper::toUserProfileResponse)
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );
  }

  public UserProfileResponse getUserProfileById(String id)
  {
    return userProfileRepository
        .findById(id)
        .map(userProfileMapper::toUserProfileResponse)
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );
  }

  @PreAuthorize("hasRole('ADMIN')")
  public List<UserProfileResponse> getAllUserProfiles() {
    return userProfileRepository.findAll()
        .stream()
        .map(userProfileMapper::toUserProfileResponse)
        .toList();
  }

  public UserProfileResponse getMyUserProfile()
  {
    return getUserProfileByUserId(SecurityUtils.getMyUserId());
  }

  public UserProfileResponse updateMyUserProfile(
      UserProfileUpdateRequest request) {

    var profile = userProfileRepository
        .findByUserId(SecurityUtils.getMyUserId())
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );

    userProfileMapper.updateUserProfile(profile, request);
    return userProfileMapper.toUserProfileResponse(userProfileRepository.save(profile));
  }
}
