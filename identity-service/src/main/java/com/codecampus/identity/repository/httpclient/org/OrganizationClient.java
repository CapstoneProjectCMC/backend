package com.codecampus.identity.repository.httpclient.org;

import com.codecampus.identity.configuration.feign.AuthenticationRequestInterceptor;
import com.codecampus.identity.configuration.feign.FeignConfigForm;
import com.codecampus.identity.dto.common.ApiResponse;
import com.codecampus.identity.dto.common.PageResponse;
import com.codecampus.identity.dto.request.org.BulkAddMembersRequest;
import com.codecampus.identity.dto.request.org.CreateOrganizationMemberRequest;
import com.codecampus.identity.dto.response.org.BlockLookupResponse;
import com.codecampus.identity.dto.response.org.BlockWithMembersLite;
import com.codecampus.identity.dto.response.org.BlocksOfUserResponse;
import com.codecampus.identity.dto.response.org.OrganizationLookupResponse;
import com.codecampus.identity.dto.response.org.PrimaryOrgResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "organization-service",
    url = "${app.services.organization}",
    configuration = {AuthenticationRequestInterceptor.class,
        FeignConfigForm.class}
)
public interface OrganizationClient {
  /**
   * GET /member/{memberId}/primary-org
   */
  @GetMapping(value = "/member/{memberId}/primary-org")
  ApiResponse<PrimaryOrgResponse> getPrimaryOrg(
      @PathVariable("memberId") String userId);

  /**
   * POST /{orgId}/member
   */
  @PostMapping(value = "/{orgId}/member")
  ApiResponse<Void> addToOrg(
      @PathVariable String orgId,
      @RequestBody CreateOrganizationMemberRequest request);

  /**
   * POST /block/{blockId}/member
   */
  @PostMapping(value = "/block/{blockId}/member")
  ApiResponse<Void> addToBlock(
      @PathVariable String blockId,
      @RequestBody CreateOrganizationMemberRequest request);

  /**
   * POST /{orgId}/members:bulk
   */
  @PostMapping(value = "/{orgId}/members:bulk")
  ApiResponse<Void> bulkAddToOrg(
      @PathVariable String orgId,
      @RequestBody BulkAddMembersRequest request);

  /**
   * POST /block/{blockId}/members:bulk
   */
  @PostMapping(value = "/block/{blockId}/members:bulk")
  ApiResponse<Void> bulkAddToBlock(
      @PathVariable String blockId,
      @RequestBody BulkAddMembersRequest request);

  /**
   * GET /user/{userId}/blocks
   */
  @GetMapping(value = "/member/{userId}/blocks")
  ApiResponse<BlocksOfUserResponse> getBlocksOfUser(
      @PathVariable String userId);

  @GetMapping("/internal/organization/resolve")
  ApiResponse<OrganizationLookupResponse> internalResolveOrganizationByName(
      @RequestParam("name") String name);

  @GetMapping("/internal/{orgId}/block/resolve")
  ApiResponse<BlockLookupResponse> internalResolveBlockByName(
      @PathVariable String orgId,
      @RequestParam("name") String name,
      @RequestParam(value = "code", required = false) String code
  );

  @GetMapping("/internal/{orgId}/blocks")
  ApiResponse<PageResponse<BlockWithMembersLite>> internalGetBlocksOfOrg(
      @PathVariable String orgId,
      @RequestParam(defaultValue = "1") int blocksPage,
      @RequestParam(defaultValue = "1000") int blocksSize,
      @RequestParam(defaultValue = "1") int membersPage,
      @RequestParam(defaultValue = "1000") int membersSize,
      @RequestParam(defaultValue = "true") boolean activeOnlyMembers,
      @RequestParam(defaultValue = "true") boolean includeUnassigned
  );
}
