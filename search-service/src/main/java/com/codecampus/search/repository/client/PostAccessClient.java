package com.codecampus.search.repository.client;

import com.codecampus.search.configuration.feign.FeignConfigForm;
import com.codecampus.search.dto.common.ApiResponse;
import com.codecampus.search.dto.common.PageResponse;
import com.codecampus.search.dto.response.PostAccessResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "post-service",
    url = "${app.services.post}",
    path = "internal",
    configuration = {FeignConfigForm.class}
)
public interface PostAccessClient {

  @GetMapping("/{postId}/access")
  ApiResponse<PageResponse<PostAccessResponseDto>> internalGetAccessByPost(
      @PathVariable("postId") String postId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "1000") int size);
}