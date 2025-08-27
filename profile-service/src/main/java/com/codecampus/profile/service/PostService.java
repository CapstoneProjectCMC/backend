package com.codecampus.profile.service;

import static com.codecampus.profile.helper.PageResponseHelper.toPageResponse;

import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.Post;
import com.codecampus.profile.entity.properties.post.Reaction;
import com.codecampus.profile.entity.properties.post.ReportedPost;
import com.codecampus.profile.entity.properties.post.SavedPost;
import com.codecampus.profile.exception.AppException;
import com.codecampus.profile.exception.ErrorCode;
import com.codecampus.profile.helper.AuthenticationHelper;
import com.codecampus.profile.repository.PostRepository;
import com.codecampus.profile.repository.UserProfileRepository;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
  UserProfileRepository userProfileRepository;
  PostRepository postRepository;
  UserProfileService userProfileService;

  @Transactional
  public void savePost(String postId) {
    getPost(postId);
    userProfileRepository.mergeSavedPost(
        AuthenticationHelper.getMyUserId(), postId, Instant.now());
  }

  @Transactional
  public void unsavePost(String postId) {
    userProfileRepository.deleteSavedPost(
        AuthenticationHelper.getMyUserId(), postId);
  }

  @Transactional
  public void reportPost(String postId, String reason) {
    getPost(postId); // đảm bảo node Post tồn tại
    userProfileRepository.mergeReportedPost(
        AuthenticationHelper.getMyUserId(), postId, reason, Instant.now());
  }

  @Transactional
  public void unReportPost(String postId) {
    getPost(postId); // đảm bảo node Post tồn tại
    userProfileRepository.deleteReportedPost(
        AuthenticationHelper.getMyUserId(), postId);
  }

  public PageResponse<SavedPost> getSavedPosts(
      int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findSavedPosts(AuthenticationHelper.getMyUserId(), pageable);

    return toPageResponse(pageData, page);
  }

  public PageResponse<Reaction> getMyReactions(
      int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findReactions(AuthenticationHelper.getMyUserId(), pageable);

    return toPageResponse(pageData, page);
  }

  public PageResponse<ReportedPost> getReportedPosts(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findReportedPosts(AuthenticationHelper.getMyUserId(),
            pageable);
    return toPageResponse(pageData, page);
  }

  public Post getPost(String postId) {
    return postRepository.findByPostId(postId)
        .orElseThrow(
            () -> new AppException(ErrorCode.POST_NOT_FOUND)
        );
  }
}
