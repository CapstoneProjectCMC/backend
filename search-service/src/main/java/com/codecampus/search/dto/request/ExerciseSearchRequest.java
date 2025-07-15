package com.codecampus.search.dto.request;

import java.time.Instant;
import java.util.Set;

public record ExerciseSearchRequest(
        String q,
        Set<String> tags,
        Integer difficulty,
        String createdBy,
        String exerciseType,
        String orgId,
        Boolean freeForOrg,
        Double minCost,
        Double maxCost,
        Instant startAfter,
        Instant endBefore,
        //TODO Đang không biết có cần search theo resourceId hay không
        Boolean allowAiQuestion,
        int page,
        int size
) {
}