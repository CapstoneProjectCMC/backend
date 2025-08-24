package events.user.data;

import java.time.Instant;
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
public class UserProfileCreationPayload {
  String firstName;
  String lastName;
  Instant dob;
  String bio;
  Boolean gender;
  String displayName;
  Integer education;
  String[] links;
  String city;

  String organizationId;
  String organizationMemberRole;
}
