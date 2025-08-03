package com.codecampus.profile.controller;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.properties.contest.ContestStatus;
import com.codecampus.profile.service.ContestService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class ContestController {

    ContestService contestService;

    // TODO Khi nào thêm đồng bộ từ submission service qua, khi có các contest assign cho student
    @GetMapping("/contests/my")
    ApiResponse<PageResponse<ContestStatus>> getMyContestStatuses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.<PageResponse<ContestStatus>>builder()
                .message("Danh sách trạng thái contest")
                .result(contestService.getMyContestStatuses(page, size))
                .build();
    }
}
