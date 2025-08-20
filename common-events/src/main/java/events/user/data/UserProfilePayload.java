package events.user.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfilePayload {
    String userId;

    // Identity snapshot (tiện cho search doc cập nhật đồng bộ)
    String username;
    String email;
    boolean active;
    Set<String> roles;

    // Profile fields
    String firstName;
    String lastName;
    Instant dob;
    String bio;
    Boolean gender;
    String displayName;
    Integer education;
    String[] links;
    String city;
    String avatarUrl;
    String backgroundUrl;

    Instant createdAt;
    Instant updatedAt;
    Instant deletedAt;
    String deletedBy;
}
