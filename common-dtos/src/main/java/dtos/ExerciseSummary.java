package dtos;

public record ExerciseSummary(
    String id,
    String title,
    String exerciseType,
    String difficulty,
    boolean visibility,
    String orgId,
    boolean created,
    boolean completed
) {
}