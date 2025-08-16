package com.codecampus.post.controller;

import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.request.CommentRequestDto;
import com.codecampus.post.dto.request.UpdateCommentDto;
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

    @PostMapping("/addComment")
    public ResponseEntity<?> addComment(
            @RequestBody CommentRequestDto dto,
            HttpServletRequest request
    ) {

        PostComment saved = postCommentService.addComment(dto, request);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Thêm bình luận thành công")
                        .build()
        );
    }

    @GetMapping("/getCmtByPostId/{postId}")
    public ResponseEntity<?> getComments(@PathVariable String postId) {
        List<CommentResponseDto> comments = postCommentService.getCommentsByPost(postId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Lấy bình luận thành công")
                        .result(comments)
                        .build()
        );
    }

    @PutMapping("/updateComment")
    public ResponseEntity<?> updateComment(
            @RequestBody UpdateCommentDto requestDto,
            HttpServletRequest request
    ) {
        postCommentService.updateComment(requestDto, request);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Bình luận chỉnh sửa thành công")
                        .build()
        );
    }

    @DeleteMapping("/deleteComment/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable String commentId,
            HttpServletRequest request
    ) {
        postCommentService.deleteComment(commentId, request);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Xoá bình luận thành công")
                        .build()
        );
    }

    private String extractUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        // giả sử bạn có customJwtDecoder như ở PostService
        return "decodedUserId"; // thay bằng code decode thực tế
    }
}

