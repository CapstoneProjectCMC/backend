package events.user;

import events.user.data.UserPayload;
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
public class UserEvent {
  Type type;
  String id;            // userId
  UserPayload payload;  // null nếu DELETED

  public enum Type { CREATED, UPDATED, DELETED, RESTORED }
}