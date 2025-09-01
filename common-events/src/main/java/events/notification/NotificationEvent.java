package events.notification;

import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class NotificationEvent {
  String channel; // "SOCKET"
  String recipient; // userId
  String templateCode; // ví dụ: ASSIGNMENT_ASSIGNED, SUBMISSION_PASSED
  Map<String, Object> param;
  String subject;
  String body;
}
