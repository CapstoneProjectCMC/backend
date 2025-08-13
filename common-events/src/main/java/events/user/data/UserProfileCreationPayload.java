package events.user.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

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
}
