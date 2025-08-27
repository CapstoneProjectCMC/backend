package dtos;

import java.time.Instant;

public record ExerciseStatusDto(
    String exerciseId,
    String studentId,
    Boolean created,
    Boolean completed,
    Instant completedAt,
    Integer attempts,
    Integer bestScore,
    Integer totalPoints
) {
}