package com.codecampus.post.dto.response;

import dtos.UserSummary;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponseDto {
  String commentId;
  String parentCommentId; // null nếu là comment gốc
  String content;
  List<CommentResponseDto> replies;
  UserSummary user;
}

