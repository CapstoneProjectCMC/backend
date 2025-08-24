package dtos;

import lombok.Builder;

import java.util.Set;

@Builder
public record UserProfileSummary(
        String userId,
        String username,
        String email,
        String displayName,
        String avatarUrl,
        String backgroundUrl,
        Boolean active,
        Set<String> roles,
        String firstName,
        String lastName,
        Boolean gender) {
}
