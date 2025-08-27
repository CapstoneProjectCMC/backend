package events.exercise;

import dtos.ExerciseStatusDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseStatusEvent {
  Type type; // UPDATED, DELETED (hoặc CREATED tuỳ service gửi)
  String id; // exerciseId
  ExerciseStatusDto payload;

  public enum Type { UPSERT, DELETED }
}
