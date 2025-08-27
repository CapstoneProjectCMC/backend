package events.contest.data;

import java.time.Instant;
import java.util.Set;
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
public class ContestPayload {
  String id;
  String title;
  Instant startTime;
  Instant endTime;
  Boolean rankPublic;
  String orgId;
  Set<String> exerciseIds;
}