package com.codecampus.profile.controller;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.ActivityWeek;
import com.codecampus.profile.service.ActivityTimeService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping("/activity")
public class ActivityTimeController {

    ActivityTimeService activityTimeService;

    @GetMapping("/weeks")
    ApiResponse<PageResponse<ActivityWeek>> getMyActivityWeeks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.<PageResponse<ActivityWeek>>builder()
                .message("Danh sách tuần hoạt động")
                .result(activityTimeService.getMyActivityWeeks(page, size))
                .build();
    }
}
