package com.codecampus.search.controller;

import com.codecampus.search.dto.common.ApiResponse;
import com.codecampus.search.dto.common.PageResponse;
import com.codecampus.search.dto.request.ExerciseSearchRequest;
import com.codecampus.search.dto.response.ExerciseSearchResponse;
import com.codecampus.search.service.ExerciseSearchService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseSearchController {

    ExerciseSearchService exerciseSearchService;

    @GetMapping("/filter")
    ApiResponse<PageResponse<ExerciseSearchResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Set<String> tags,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) String exerciseType,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) Boolean freeForOrg,
            @RequestParam(required = false) Double minCost,
            @RequestParam(required = false) Double maxCost,
            @RequestParam(required = false) Instant startAfter,
            @RequestParam(required = false) Instant endBefore,
            @RequestParam(required = false) Boolean allowAiQuestion,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        ExerciseSearchRequest request = new ExerciseSearchRequest(
                q, tags, difficulty, createdBy, exerciseType, orgId,
                freeForOrg, minCost, maxCost, startAfter, endBefore,
                allowAiQuestion, page, size);

        return ApiResponse.<PageResponse<ExerciseSearchResponse>>builder()
                .result(exerciseSearchService.searchExercise(request))
                .message("Tìm kiếm thành công!")
                .build();
    }
}
