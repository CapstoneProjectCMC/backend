package event.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class ExercisePayload {
    String id;
    String exerciseType;             // QUIZ | CODING
    String title;
    String description;
    Set<String> tags;
    Integer difficulty;
    String createdBy;
    Double cost;
    String orgId;
    Boolean freeForOrg;
    Instant startTime;
    Instant endTime;
    Integer duration;
    Set<String> resourceIds;
    Boolean allowAiQuestion;
    Instant createdAt;
    Instant updatedAt;
    Instant deletedAt;
    String updatedBy;
    String deletedBy;
}
