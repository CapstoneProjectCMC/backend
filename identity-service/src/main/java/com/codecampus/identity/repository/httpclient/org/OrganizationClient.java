package com.codecampus.identity.repository.httpclient.org;

import com.codecampus.identity.configuration.config.AuthenticationRequestInterceptor;
import com.codecampus.identity.dto.common.ApiResponse;
import com.codecampus.identity.dto.request.org.CreateOrganizationMemberRequest;
import com.codecampus.identity.dto.response.org.PrimaryOrgResponse;
import java.util.List;
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
  @GetMapping(
      value = "/user/{userId}/primary",
      produces = MediaType.APPLICATION_JSON_VALUE)
  ApiResponse<PrimaryOrgResponse> getPrimaryOrg(
      @PathVariable("userId") String userId);

  @PostMapping(
      value = "/add",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  ApiResponse<Void> createMembership(
      @RequestBody CreateOrganizationMemberRequest request);

  @PostMapping(
      value = "/add-v2",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  ApiResponse<Void> createMembershipV2(
      @RequestBody CreateOrganizationMemberRequest request);

  // bulk
  @PostMapping(value = "/bulk-add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  ApiResponse<Void> bulkCreateMembership(
      @RequestBody List<CreateOrganizationMemberRequest> requests);
}
