package com.codecampus.post.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRequestDto {
    private String postId; // for update post
    private String title;
    private String orgId;
    private String content;
    private boolean isPublic;
    private boolean allowComment;
    private String postType;
    private String hashtag;
    private String status;
    private AddFileDocumentDto fileDocument; // for add file document
//    private PostAccess postAccess;
}
