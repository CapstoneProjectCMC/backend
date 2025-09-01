package com.codecampus.profile.service;

import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.properties.exercise.SavedExercise;
import com.codecampus.profile.entity.properties.post.SavedPost;
import com.codecampus.profile.exception.AppException;
import com.codecampus.profile.exception.ErrorCode;
import com.codecampus.profile.helper.AuthenticationHelper;
import com.codecampus.profile.helper.PageResponseHelper;
import com.codecampus.profile.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VisibilityGuard {
  UserProfileRepository userProfileRepository;


  public void assertCanViewSavedOf(String targetUserId) {
    String me = AuthenticationHelper.getMyUserId();
    if (me == null) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }
    if (me.equals(targetUserId)) {
      return;
    }

    if (userProfileRepository.isBlockedEitherWay(me, targetUserId)) {
      throw new AppException(ErrorCode.UNAUTHORIZED); // or FORBIDDEN
    }
    if (!userProfileRepository.existsFollow(me, targetUserId)) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }
  }

  public PageResponse<SavedPost> savedPostsOf(
      String userId,
      int page, int size) {
    assertCanViewSavedOf(userId);
    Pageable p = PageRequest.of(page - 1, size);
    var data = userProfileRepository
        .findSavedPosts(userId, p);
    return PageResponseHelper.toPageResponse(
        data, page);
  }

  public PageResponse<SavedExercise> savedExercisesOf(
      String userId,
      int page, int size) {
    assertCanViewSavedOf(userId);
    Pageable p = PageRequest.of(page - 1, size);
    var data = userProfileRepository
        .findSavedExercises(userId, p);
    return PageResponseHelper.toPageResponse(
        data, page);
  }
}