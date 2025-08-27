package dtos;


import java.time.Instant;

public record ContestStatusDto(
    String contestId,
    String studentId,
    String state,
    Integer rank,
    Double score,
    Instant updatedAt
) {
}