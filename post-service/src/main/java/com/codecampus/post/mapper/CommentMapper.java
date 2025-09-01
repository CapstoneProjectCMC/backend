package com.codecampus.post.mapper;

import com.codecampus.post.dto.response.CommentCreatedDto;
import com.codecampus.post.entity.PostComment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
  default CommentCreatedDto toCreatedDtoFromPostComment(
      PostComment c) {
    return CommentCreatedDto.builder()
        .commentId(c.getCommentId())
        .parentCommentId(c.getParentComment() == null ? null
            : c.getParentComment().getCommentId())
        .content(c.getContent())
        .createdAt(c.getCreatedAt() != null ? c.getCreatedAt() : null)
        .build();
  }
}
