package com.codecampus.post.dto.response;

import com.codecampus.post.entity.PostAccess;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostResponseDto {
    private String postId;
    private String userId;
    private String username;     // lấy từ ProfileResponseDto
    private String avatarUrl;    // lấy từ ProfileResponseDto
    private String orgId;
    private String postType; //global, organization, group, etc.
    private String title;
    private String content;
    private Boolean isPublic;
    private Boolean allowComment;
    private String hashtag;
    private String status;
    private String imagesUrls;
    private List<PostAccess> accesses;
    private String createdAt;
}
