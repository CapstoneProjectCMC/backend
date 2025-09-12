package com.codecampus.post.helper;

import com.codecampus.post.dto.response.CommentResponseDto;
import com.codecampus.post.entity.PostComment;
import com.codecampus.post.service.cache.UserSummaryCacheService;
import dtos.UserSummary;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CommentHelper {

  UserSummaryCacheService userSummaryCacheService;

  public CommentResponseDto toCommentResponseDto(
      PostComment c,
      Map<String, UserSummary> map) {
    return toCommentResponseDto(c, map, List.of());
  }

  public CommentResponseDto toCommentResponseDto(
      PostComment c,
      Map<String, UserSummary> map,
      List<CommentResponseDto> replies) {
    return CommentResponseDto.builder()
        .commentId(c.getCommentId())
        .parentCommentId(c.getParentComment() == null ? null
            : c.getParentComment().getCommentId())
        .content(c.getContent())
        .replies(replies) // chỉ 1 cấp reply
        .user(map.getOrDefault(c.getUserId(),
            userSummaryCacheService.getOrLoad(c.getUserId())))
        .createdAt(c.getCreatedAt() != null ? c.getCreatedAt() : null)
        .build();
  }

  public CommentResponseDto toCommentResponseDto(PostComment c) {
    return CommentResponseDto.builder()
        .commentId(c.getCommentId())
        .parentCommentId(c.getParentComment() == null ? null :
            c.getParentComment().getCommentId())
        .content(c.getContent())
        .replies(null) // không cần trong push realtime
        .user(userSummaryCacheService.getOrLoad(c.getUserId()))
        .createdAt(c.getCreatedAt())
        .build();
  }
}
