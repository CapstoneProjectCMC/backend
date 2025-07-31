package com.codecampus.profile.controller;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.service.FamilyService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping("/family")
public class FamilyController {

    FamilyService familyService;

    @PostMapping("/{parentId}/children/{childId}")
    ApiResponse<Void> addChild(
            @PathVariable String parentId,
            @PathVariable String childId) {

        familyService.addChild(parentId, childId);
        return ApiResponse.<Void>builder()
                .message("Thêm con thành công")
                .build();
    }

    @DeleteMapping("/{parentId}/children/{childId}")
    ApiResponse<Void> removeChild(
            @PathVariable String parentId,
            @PathVariable String childId) {

        familyService.removeChild(parentId, childId);
        return ApiResponse.<Void>builder()
                .message("Đã xóa con")
                .build();
    }

    @GetMapping("/{parentId}/children")
    ApiResponse<List<UserProfile>> children(
            @PathVariable String parentId) {
        return ApiResponse.<List<UserProfile>>builder()
                .message("Danh sách con")
                .result(familyService.getChildren(parentId))
                .build();
    }
}
