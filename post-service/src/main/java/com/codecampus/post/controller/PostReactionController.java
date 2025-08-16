package com.codecampus.post.controller;

import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.request.PostReactionRequestDto;
import com.codecampus.post.service.PostReactionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/postReaction")
@RequiredArgsConstructor
public class PostReactionController {

    private final PostReactionService postReactionService;

    // Toggle reaction (upvote / downvote)
    @PostMapping("/toggle")
    public ResponseEntity<?> toggleReaction(@RequestBody PostReactionRequestDto requestDto,
                                                 HttpServletRequest request) {
        postReactionService.toggleReaction(requestDto, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Reaction toggled successfully")
                .build());
    }

    // Get reaction count cho post
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getPostReactions(@PathVariable String postId) {
        Map<String, Long> counts = postReactionService.getReactionCount(postId, null);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Reaction counts retrieved successfully")
                .result(counts)
                .build());
    }

    // Get reaction count cho comment trong post
    @GetMapping("/post/{postId}/comment/{commentId}")
    public ResponseEntity<?> getCommentReactions(@PathVariable String postId,
                                                                 @PathVariable String commentId) {
        Map<String, Long> counts = postReactionService.getReactionCount(postId, commentId);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Reaction counts retrieved successfully")
                .result(counts)
                .build());
    }
}
