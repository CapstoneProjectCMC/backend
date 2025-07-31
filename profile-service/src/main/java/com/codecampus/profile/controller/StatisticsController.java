package com.codecampus.profile.controller;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.dto.response.UserPostStats;
import com.codecampus.profile.service.StatisticsService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping("/stats")
public class StatisticsController {

    StatisticsService statisticsService;

    @GetMapping("/post/{userId}")
    ApiResponse<UserPostStats> getPostStats(
            @PathVariable String userId) {
        return ApiResponse.<UserPostStats>builder()
                .message("Thống kê bài viết")
                .result(statisticsService.getPostStats(userId))
                .build();
    }
}
