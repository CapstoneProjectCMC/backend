package com.codecampus.identity.repository.httpclient.profile;

import com.codecampus.identity.configuration.config.AuthenticationRequestInterceptor;
import com.codecampus.identity.dto.common.ApiResponse;
import com.codecampus.identity.dto.request.profile.UserProfileCreationRequest;
import com.codecampus.identity.dto.request.profile.UserProfileUpdateRequest;
import com.codecampus.identity.dto.response.profile.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "profile-service",
        url = "${app.services.profile}",
        configuration = {AuthenticationRequestInterceptor.class},
        path = "/internal"
)
public interface ProfileClient {
    @PostMapping(
            value = "/user",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ApiResponse<UserProfileResponse> internalCreateUserProfile(
            @RequestBody UserProfileCreationRequest request);

    @PatchMapping(
            value = "/user/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ApiResponse<Void> internalUpdateProfileByUserId(
            @PathVariable("userId") String userId,
            @RequestBody UserProfileUpdateRequest request);

    @DeleteMapping(
            value = "/user/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ApiResponse<Void> internalSoftDeleteByUserId(
            @PathVariable("userId") String userId,
            @RequestParam(value = "deletedBy", required = false)
            String deletedBy);

    @PatchMapping(
            value = "/user/{userId}/restore",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ApiResponse<Void> internalRestoreProfile(
            @PathVariable("userId") String userId);
}
