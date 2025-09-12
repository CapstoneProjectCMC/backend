package com.codecampus.search.dto.response;


import dtos.UserSummary;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record PostSearchResponse(
    String postId,
    UserSummary user,
    String orgId,
    String postType,
    String title,
    String content,
    Boolean isPublic,
    Boolean allowComment,
    String hashtag,
    String status,
    List<String> imagesUrls,
    String createdAt,
    long commentCount,
    long upvoteCount,
    long downvoteCount
) {
}