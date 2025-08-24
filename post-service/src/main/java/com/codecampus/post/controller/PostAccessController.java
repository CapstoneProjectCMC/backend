package com.codecampus.post.controller;

import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.request.PostAccessRequestDto;
import com.codecampus.post.service.PostAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/postAccess")
@RequiredArgsConstructor
public class PostAccessController {

  private final PostAccessService postAccessService;

  @PostMapping("/addAccess")
  public ResponseEntity<?> addOrUpdateAccess(
      @RequestBody PostAccessRequestDto dto) {
    postAccessService.saveOrUpdateAccess(dto);
    return ResponseEntity.ok(ApiResponse.builder()
        .message("Access list updated successfully")
        .build());
  }

  @DeleteMapping("/deleteAccess")
  public ResponseEntity<?> deleteAccess(
      @RequestBody PostAccessRequestDto userIds) {
    postAccessService.deleteAccess(userIds);
    return ResponseEntity.ok(ApiResponse.builder()
        .message("Access list deleted successfully")
        .build());
  }

  @GetMapping("/getPostAccess/{postId}")
  public ResponseEntity<?> getAccessList(@PathVariable String postId) {
    return ResponseEntity.ok(ApiResponse.builder()
        .message("Access list retrieved successfully")
        .result(postAccessService.getAccessByPostId(postId))
        .build());
  }
}
