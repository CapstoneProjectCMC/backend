package dtos;

import java.util.Set;
import lombok.Builder;

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