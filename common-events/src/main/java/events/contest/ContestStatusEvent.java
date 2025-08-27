package events.contest;

import dtos.ContestStatusDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContestStatusEvent {
  Type type;
  String id; // contestId
  ContestStatusDto payload;

  public enum Type { UPSERT, DELETED }
}