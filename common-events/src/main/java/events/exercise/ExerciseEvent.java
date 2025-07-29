package events.exercise;

import events.exercise.data.ExercisePayload;
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
public class ExerciseEvent {
    Type type;
    String id;

    ExercisePayload payload; // null nếu DELETED

    public enum Type { CREATED, UPDATED, DELETED }
}
