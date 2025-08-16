package com.codecampus.post.controller;

import com.codecampus.post.dto.request.PostAccessRequestDto;
import com.codecampus.post.entity.PostAccess;
import com.codecampus.post.service.PostAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post-access")
@RequiredArgsConstructor
public class PostAccessController {

    private final PostAccessService postAccessService;

    @PostMapping
    public ResponseEntity<?> addOrUpdateAccess(@RequestBody PostAccessRequestDto dto) {
        postAccessService.saveOrUpdateAccess(dto);
        return ResponseEntity.ok("Access list updated successfully");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAccess(
            @RequestParam String postId,
            @RequestBody List<String> userIds) {
        postAccessService.deleteAccess(postId, userIds);
        return ResponseEntity.ok("Access list deleted successfully");
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<?>> getAccessList(@PathVariable String postId) {
        return ResponseEntity.ok(postAccessService.getAccessByPostId(postId));
    }
}
