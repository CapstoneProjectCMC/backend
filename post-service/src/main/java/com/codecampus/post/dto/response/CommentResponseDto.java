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
    private String userId;
    private String content;
    private List<CommentResponseDto> replies;
}

