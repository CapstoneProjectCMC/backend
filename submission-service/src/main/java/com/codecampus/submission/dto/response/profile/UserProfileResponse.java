package com.codecampus.submission.dto.response.profile;


import lombok.Builder;

import java.util.Set;

@Builder
public record UserProfileResponse(
        String userId,
        String username,
        String email,
        String displayName,
        String avatarUrl,
        Boolean active,
        Set<String> roles
) {
}