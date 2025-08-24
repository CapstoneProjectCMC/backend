package events.user.data;

import java.time.Instant;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPayload {
  String userId;     // id của identity -> dùng làm @Id trong profile
  String username;
  String email;
  boolean active;    // enabled của identity

  Set<String> roles;       // ["ADMIN","USER",...]
  Instant createdAt;
  Instant updatedAt;
}