package com.codecampus.profile.service;

import static com.codecampus.profile.utils.PageResponseUtils.toPageResponse;

import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.Post;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.entity.properties.post.SavedPost;
import com.codecampus.profile.exception.AppException;
import com.codecampus.profile.exception.ErrorCode;
import com.codecampus.profile.repository.PostRepository;
import com.codecampus.profile.repository.UserProfileRepository;
import com.codecampus.profile.utils.SecurityUtils;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService
{
  UserProfileRepository userProfileRepository;
  PostRepository postRepository;

  UserProfileService userProfileService;

  public void savePost(String postId) {
    UserProfile myProfile = userProfileService.getUserProfile();

    Post post = getPost(postId);

    SavedPost savedPost = SavedPost.builder()
        .saveAt(Instant.now())
        .post(post)
        .build();

    myProfile.getSavedPosts().add(savedPost);
    userProfileRepository.save(myProfile);
  }

  public void unsavePost(String postId)
  {
    UserProfile myProfile = userProfileService.getUserProfile();

    myProfile.getSavedPosts()
        .removeIf(post -> post.getId().equals(postId));

    userProfileRepository.save(myProfile);
  }

  public PageResponse<SavedPost> getSavedPosts(
      int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findSavedPosts(SecurityUtils.getMyUserId(), pageable);

    return toPageResponse(pageData, page);
  }

  public Post getPost(String postId)
  {
    return postRepository.findByPostId(postId)
        .orElseThrow(
            () -> new AppException(ErrorCode.POST_NOT_FOUND)
        );
  }
}
