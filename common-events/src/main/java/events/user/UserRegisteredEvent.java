package events.user;

import events.user.data.UserPayload;
import events.user.data.UserProfileCreationPayload;
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
public class UserRegisteredEvent {
  String id;
  UserPayload user;
  UserProfileCreationPayload profile;
}
