package events.notification;


import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationStatusEvent {

  Type type;
  /**
   * Bắt buộc: user nhận thông báo (room/socket) và phạm vi update
   */
  String recipient;
  /**
   * Tuỳ chọn: 1 id đơn
   */
  String id;
  /**
   * Tuỳ chọn: nhiều id
   */
  Set<String> ids;
  /**
   * Tuỳ chọn: nếu type=MARK_ALL_READ thì đánh dấu tất cả thông báo của recipient
   * có createdAt <= before (nếu null thì tất cả).
   */
  Instant before;
  /**
   * Thời điểm đánh dấu, mặc định Instant.now() nếu null
   */
  Instant at;

  public enum Type {
    MARK_READ,        // Đánh dấu READ cho 1 hoặc nhiều id
    MARK_UNREAD,      // Đánh dấu UNREAD cho 1 hoặc nhiều id
    MARK_ALL_READ     // Đánh dấu READ cho tất cả (có thể kèm mốc thời gian)
  }
}