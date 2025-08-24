package com.codecampus.profile.repository.client;

import com.codecampus.profile.config.AuthenticationRequestInterceptor;
import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.dto.request.org.OrganizationMemberDto;
import com.codecampus.profile.dto.response.org.PrimaryOrgDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "organization-service",
        url = "${app.services.organization}",
        configuration = {AuthenticationRequestInterceptor.class},
        path = "/api/OrganizationMember"
)
public interface OrganizationClient {

    @PostMapping(
            value = "/join/organization/{organizationId}"
    )
    ApiResponse<OrganizationMemberDto> joinOrganization(
            @PathVariable("organizationId") String organizationId);

    @GetMapping(
            value = "/user/{userId}"
    )
    ApiResponse<List<OrganizationMemberDto>> membershipsOfUser(
            @PathVariable("userId") String userId,
            @RequestParam(value = "scopeType", required = false)
            String scopeType);

    @GetMapping(
            "/user/{userId}/primary-org"
    )
    ApiResponse<PrimaryOrgDto> primaryOrg(
            @PathVariable("userId") String userId);
}