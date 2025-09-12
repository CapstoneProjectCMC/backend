package com.codecampus.search.repository.client;

import com.codecampus.search.configuration.feign.AuthenticationRequestInterceptor;
import com.codecampus.search.configuration.feign.FeignConfigForm;
import com.codecampus.search.dto.common.ApiResponse;
import com.codecampus.search.dto.common.PageResponse;
import com.codecampus.search.dto.response.BlockWithMembersPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "organization-service",
    url = "${app.services.organization}",
    configuration = {AuthenticationRequestInterceptor.class,
        FeignConfigForm.class},
    path = "/internal"
)
public interface OrganizationClient {

  @GetMapping("/{orgId}/blocks")
  ApiResponse<PageResponse<BlockWithMembersPageResponse>> internalGetBlocksOfOrg(
      @PathVariable("orgId") String orgId,
      @RequestParam(defaultValue = "1") int blocksPage,
      @RequestParam(defaultValue = "10") int blocksSize,
      @RequestParam(defaultValue = "1") int membersPage,
      @RequestParam(defaultValue = "10") int membersSize,
      @RequestParam(defaultValue = "true") boolean activeOnlyMembers,
      @RequestParam(defaultValue = "true") boolean includeUnassigned
  );
}