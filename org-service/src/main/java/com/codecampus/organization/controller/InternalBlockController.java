package com.codecampus.organization.controller;

import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.common.PageResponse;
import com.codecampus.organization.dto.response.BlockWithMembersPageResponse;
import com.codecampus.organization.dto.response.IdNameResponse;
import com.codecampus.organization.service.BlockService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/internal")
public class InternalBlockController {

  BlockService blockService;

  @GetMapping("/{orgId}/blocks")
  ApiResponse<PageResponse<BlockWithMembersPageResponse>> internalGetBlocksOfOrg(
      @PathVariable String orgId,
      @RequestParam(defaultValue = "1") int blocksPage,
      @RequestParam(defaultValue = "10") int blocksSize,
      @RequestParam(defaultValue = "1") int membersPage,
      @RequestParam(defaultValue = "10") int membersSize,
      @RequestParam(defaultValue = "true") boolean activeOnlyMembers,
      @RequestParam(defaultValue = "true") boolean includeUnassigned) {
    return ApiResponse.<PageResponse<BlockWithMembersPageResponse>>builder()
        .message("Get thông tin các khối trong tổ chức thành công!")
        .result(blockService.getBlocksOfOrg(orgId, blocksPage, blocksSize,
            membersPage, membersSize, activeOnlyMembers, includeUnassigned))
        .build();
  }

  @GetMapping("/{orgId}/block/resolve")
  public ApiResponse<IdNameResponse> internalResolveBlockByName(
      @PathVariable String orgId,
      @RequestParam("name") String name,
      @RequestParam(value = "code", required = false) String code
  ) {
    return ApiResponse.<IdNameResponse>builder()
        .result(blockService.resolveBlockByName(orgId, name, code))
        .build();
  }
}
