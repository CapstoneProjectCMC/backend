package com.codecampus.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private String commentId;
    private String parentCommentId; // null nếu là comment gốc
    private String content;
    private List<CommentResponseDto> replies;
    private ProfileResponseDto user;
}

