package com.codecampus.post.helper;

import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.request.AddFileDocumentDto;
import com.codecampus.post.dto.response.PostResponseDto;
import com.codecampus.post.dto.response.file.UploadedFileResponse;
import com.codecampus.post.entity.Post;
import com.codecampus.post.repository.PostCommentRepository;
import com.codecampus.post.repository.PostReactionRepository;
import com.codecampus.post.repository.httpClient.FileServiceClient;
import com.codecampus.post.service.cache.UserSummaryCacheService;
import dtos.UserSummary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PostHelper {

  UserSummaryCacheService userSummaryCacheService;
  PostCommentRepository postCommentRepository;
  PostReactionRepository postReactionRepository;
  FileServiceClient fileServiceClient;

  public PostResponseDto toPostResponseDtoFromPost(Post post) {
    UserSummary summary = userSummaryCacheService.getOrLoad(post.getUserId());

    long commentCount = postCommentRepository
        .countByPost_PostIdAndDeletedAtIsNull(post.getPostId());
    long up = postReactionRepository
        .countByPost_PostIdAndEmojiType(post.getPostId(), "upvote");
    long down = postReactionRepository
        .countByPost_PostIdAndEmojiType(post.getPostId(), "downvote");

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
        .fileUrls(post.getFileUrls())
        .accesses(post.getAccesses())
        .createdAt(
            post.getCreatedAt() != null ? post.getCreatedAt().toString() : null)
        .commentCount(commentCount)
        .upvoteCount(up)
        .downvoteCount(down)
        .build();
  }

  public boolean canView(Post p, String userId) {
    if (userId != null && userId.equals(p.getUserId())) {
      return true;
    }
    if (Boolean.TRUE.equals(p.getIsPublic())) {
      return true;
    }
    if ("Global".equalsIgnoreCase(p.getPostType())) {
      return true;
    }
    var acc = p.getAccesses();
    if (acc == null) {
      return false;
    }
    return acc.stream()
        .anyMatch(a -> userId != null && userId.equals(a.getUserId())
            && (a.getIsExcluded() == null || !a.getIsExcluded()));
  }

  public List<String> uploadAll(AddFileDocumentDto fileDoc) {
    if (fileDoc == null) {
      return Collections.emptyList();
    }

    List<String> urls = new ArrayList<>();

    // Nhiều file
    if (fileDoc.getFiles() != null && !fileDoc.getFiles().isEmpty()) {
      for (var f : fileDoc.getFiles()) {
        if (f == null || f.isEmpty()) {
          continue;
        }

        AddFileDocumentDto one = new AddFileDocumentDto();
        one.setFile(f);
        one.setCategory(fileDoc.getCategory());
        one.setDescription(fileDoc.getDescription());
        one.setTags(fileDoc.getTags());
        one.setLectureVideo(fileDoc.isLectureVideo());
        one.setTextbook(fileDoc.isTextbook());
        one.setOrgId(fileDoc.getOrgId());

        var api = fileServiceClient.uploadFile(one);
        Optional.ofNullable(api)
            .map(ApiResponse::getResult)
            .map(UploadedFileResponse::getUrl)
            .ifPresent(urls::add);
      }
    }

    return urls;
  }
}
