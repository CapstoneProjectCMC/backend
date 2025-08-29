package com.codecampus.identity.repository.httpclient.org;

import com.codecampus.identity.configuration.config.AuthenticationRequestInterceptor;
import com.codecampus.identity.dto.common.ApiResponse;
import com.codecampus.identity.dto.request.org.BulkAddMembersRequest;
import com.codecampus.identity.dto.request.org.CreateOrganizationMemberRequest;
import com.codecampus.identity.dto.response.org.BlocksOfUserResponse;
import com.codecampus.identity.dto.response.org.PrimaryOrgResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "organization-service",
    url = "${app.services.organization}",
    configuration = {AuthenticationRequestInterceptor.class},
    path = "/api/OrganizationMember"
)
public interface OrganizationClient {
  /**
   * GET /member/{memberId}/primary-org
   */
  @GetMapping(value = "/member/{memberId}/primary-org", produces = MediaType.APPLICATION_JSON_VALUE)
  ApiResponse<PrimaryOrgResponse> getPrimaryOrg(
      @PathVariable("memberId") String userId);

  /**
   * POST /{orgId}/member
   */
  @PostMapping(value = "/{orgId}/member", consumes = MediaType.APPLICATION_JSON_VALUE)
  ApiResponse<Void> addToOrg(
      @PathVariable String orgId,
      @RequestBody CreateOrganizationMemberRequest request);

  /**
   * POST /block/{blockId}/member
   */
  @PostMapping(value = "/block/{blockId}/member", consumes = MediaType.APPLICATION_JSON_VALUE)
  ApiResponse<Void> addToBlock(
      @PathVariable String blockId,
      @RequestBody CreateOrganizationMemberRequest request);

  /**
   * POST /{orgId}/members:bulk
   */
  @PostMapping(
      value = "/{orgId}/members:bulk",
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  ApiResponse<Void> bulkAddToOrg(
      @PathVariable String orgId,
      @RequestBody BulkAddMembersRequest request);

  /**
   * POST /block/{blockId}/members:bulk
   */
  @PostMapping(
      value = "/block/{blockId}/members:bulk",
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  ApiResponse<Void> bulkAddToBlock(
      @PathVariable String blockId,
      @RequestBody BulkAddMembersRequest request);

  /**
   * GET /user/{userId}/blocks
   */
  @GetMapping(
      value = "/user/{userId}/blocks",
      produces = MediaType.APPLICATION_JSON_VALUE)
  ApiResponse<BlocksOfUserResponse> getBlocksOfUser(
      @PathVariable String userId);
}
