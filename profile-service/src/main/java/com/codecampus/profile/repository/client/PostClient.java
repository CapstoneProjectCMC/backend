package com.codecampus.profile.repository.client;

import com.codecampus.profile.config.AuthenticationRequestInterceptor;
import com.codecampus.profile.dto.common.ApiResponse;
import dtos.PostSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "post-service",
    url = "${app.services.post}",
    path = "/internal",
    configuration = AuthenticationRequestInterceptor.class
)
public interface PostClient {
  @GetMapping("/post/{postId}/summary")
  ApiResponse<PostSummary> internalGetPostSummary(
      @PathVariable("postId") String postId);
}