package com.codecampus.post.helper;

import com.codecampus.post.dto.response.PostResponseDto;
import com.codecampus.post.entity.Post;
import com.codecampus.post.service.cache.UserSummaryCacheService;
import dtos.UserSummary;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PostHelper {

  UserSummaryCacheService userSummaryCacheService;

  public PostResponseDto toPostResponseDtoFromPost(Post post) {
    UserSummary summary = userSummaryCacheService.getOrLoad(post.getUserId());

    return PostResponseDto.builder()
        .postId(post.getPostId())
        .user(summary)
        .orgId(post.getOrgId())
        .postType(post.getPostType())
        .title(post.getTitle())
        .content(post.getContent())
        .isPublic(post.getIsPublic())
        .allowComment(post.getAllowComment())
        .hashtag(post.getHashtag())
        .status(post.getStatus())
        .imagesUrls(post.getImagesUrls())
        .accesses(post.getAccesses())
        .createdAt(
            post.getCreatedAt() != null ? post.getCreatedAt().toString() : null)
        .build();
  }

  public boolean canView(Post p, String userId) {
    if (Boolean.TRUE.equals(p.getIsPublic())) {
      return true;
    }
    if ("Global".equalsIgnoreCase(p.getPostType())) {
      return true;
    }
    if (p.getAccesses() == null) {
      return false;
    }
    return p.getAccesses().stream()
        .anyMatch(a -> userId.equals(a.getUserId())
            && (a.getIsExcluded() == null || !a.getIsExcluded()));
  }
}
