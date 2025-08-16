package com.codecampus.post.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    private String postId;
    private String parentCommentId; // null nếu là comment gốc
    private String content;
}
