package com.codecampus.post.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAccessRequestDto {
    private String postId;
    private List<String> userIds;
    private Boolean isExcluded;
}
