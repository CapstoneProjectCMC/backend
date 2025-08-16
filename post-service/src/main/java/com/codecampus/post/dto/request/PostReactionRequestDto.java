package com.codecampus.post.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostReactionRequestDto {
    private String postId;
    private String userId;
    private String commentId; // for comment reactions, can be null if reacting to the post itself
    private String reactionType = "like"; // e.g., "like", "love", "laugh", etc.// true if the user has reacted, false otherwise
}

