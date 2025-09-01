package com.codecampus.search.repository.client;

import com.codecampus.search.dto.common.ApiResponse;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "post-service-stats",
    url = "${app.services.post}"
)
public interface PostStatsClient {

  @GetMapping("/{postId}/reactions/count")
  ApiResponse<Map<String, Long>> reactionCounts(
      @PathVariable("postId") String postId);

  @GetMapping("/{postId}/comments/count")
  ApiResponse<Long> commentCount(
      @PathVariable("postId") String postId);
}