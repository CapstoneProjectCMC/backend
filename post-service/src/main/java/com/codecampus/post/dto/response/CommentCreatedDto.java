package com.codecampus.post.dto.response;

import lombok.Builder;

@Builder
public record CommentCreatedDto(
    String commentId,
    String parentCommentId,
    String content) {
}