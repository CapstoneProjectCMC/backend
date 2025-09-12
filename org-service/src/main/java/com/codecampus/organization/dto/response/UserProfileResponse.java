package com.codecampus.organization.dto.response;

import java.util.Set;
import lombok.Builder;

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