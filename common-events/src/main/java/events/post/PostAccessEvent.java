package events.post;

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
public class PostAccessEvent {
  Type type;
  String postId;
  String userId;     // null nếu bulk
  Boolean isExcluded;

  public enum Type { UPSERT, BULK_DELETE, DELETE_ALL_FOR_POST }
}