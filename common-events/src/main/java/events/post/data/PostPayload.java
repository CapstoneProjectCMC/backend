package events.post.data;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostPayload {
  String id;
  String userId;
  String orgId;
  String postType; // Global | Organization | ...
  String title;
  String content; // có thể bỏ nếu không muốn “nặng”
  Boolean isPublic;
  Boolean allowComment;
  String hashtag;
  String status;
  List<String> fileUrls;

  Instant createdAt;
  Instant updatedAt;
  Instant deletedAt;
  String deletedBy;
}