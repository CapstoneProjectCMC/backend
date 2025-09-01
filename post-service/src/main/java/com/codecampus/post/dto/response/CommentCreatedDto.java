package com.codecampus.post.dto.response;

import java.time.Instant;
import lombok.Builder;

@Builder
public record CommentCreatedDto(
    String commentId,
    String parentCommentId,
    String content,
    Instant createdAt) {
}