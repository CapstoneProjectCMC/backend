package dtos;

import java.time.Instant;
import lombok.Builder;

@Builder
public record ContestSummary(
    String id,
    String title,
    Instant startTime,
    Instant endTime,
    boolean rankPublic,
    String orgId
) {
}