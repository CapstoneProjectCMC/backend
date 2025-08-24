package events.user;


import events.user.data.UserProfilePayload;
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
public class UserProfileEvent {
  Type type;
  String id;                    // userId
  UserProfilePayload payload;   // null nếu DELETED

  public enum Type { UPDATED, DELETED, RESTORED }
}