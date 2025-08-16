package com.codecampus.post.controller;

import com.codecampus.post.dto.request.CommentRequestDto;
import com.codecampus.post.dto.response.CommentResponseDto;
import com.codecampus.post.entity.PostComment;
import com.codecampus.post.service.PostCommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class PostCommentController {

    private final PostCommentService postCommentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> addComment(
            @RequestBody CommentRequestDto dto,
            HttpServletRequest request
    ) {
        String userId = extractUserIdFromToken(request);
        PostComment saved = postCommentService.addComment(dto, userId);
        return ResponseEntity.ok(
                new CommentResponseDto(saved.getCommentId(), saved.getUserId(), saved.getContent(), List.of())
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable String postId) {
        return ResponseEntity.ok(postCommentService.getCommentsByPost(postId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateComment(
            @PathVariable String commentId,
            @RequestBody String newContent,
            HttpServletRequest request
    ) {
        String userId = extractUserIdFromToken(request);
        postCommentService.updateComment(commentId, userId, newContent);
        return ResponseEntity.ok("Comment updated successfully");
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable String commentId,
            HttpServletRequest request
    ) {
        String userId = extractUserIdFromToken(request);
        postCommentService.deleteComment(commentId, userId);
        return ResponseEntity.ok("Comment deleted successfully");
    }

    private String extractUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        // giả sử bạn có customJwtDecoder như ở PostService
        return "decodedUserId"; // thay bằng code decode thực tế
    }
}

