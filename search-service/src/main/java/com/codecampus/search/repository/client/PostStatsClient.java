package com.codecampus.search.repository.client;

import com.codecampus.search.configuration.feign.FeignConfigForm;
import com.codecampus.search.dto.common.ApiResponse;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "post-service-stats",
    url = "${app.services.post}",
    path = "internal",
    configuration = {FeignConfigForm.class}
)
public interface PostStatsClient {

  @GetMapping("/{postId}/reactions/count")
  ApiResponse<Map<String, Long>> internalReactionCounts(
      @PathVariable("postId") String postId);

  @GetMapping("/{postId}/comments/count")
  ApiResponse<Long> internalCommentCount(
      @PathVariable("postId") String postId);
}