package com.codecampus.post.dto.response;

import com.codecampus.post.entity.PostAccess;
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
public class PostResponseDto {
  String postId;
  UserSummary user;
  String orgId;
  String postType; //global, organization, group, etc.
  String title;
  String content;
  Boolean isPublic;
  Boolean allowComment;
  String hashtag;
  String status;
  List<String> fileUrls;
  List<PostAccess> accesses;
  String createdAt;
  long commentCount;
  long upvoteCount;
  long downvoteCount;
}
