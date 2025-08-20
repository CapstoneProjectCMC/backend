package dtos;

import lombok.Builder;

import java.util.Set;

@Builder
public record UserSummary(
        String userId,
        String username,
        String email,
        String displayName,
        String avatarUrl,
        Boolean active,
        Set<String> roles
) {
}