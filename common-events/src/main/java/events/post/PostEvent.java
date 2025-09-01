package events.post;

import events.post.data.PostPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEvent {
  Type type;
  String id;          // postId
  PostPayload payload; // null nếu DELETED

  public enum Type { CREATED, UPDATED, DELETED }
}