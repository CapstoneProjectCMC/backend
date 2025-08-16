package com.codecampus.post.controller;

import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.request.AddFileDocumentDto;
import com.codecampus.post.dto.request.PostRequestDto;
import com.codecampus.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/getAllAccessiblePosts")
    public ResponseEntity<?> getAllAccessiblePosts(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Success")
                .result(postService.getAllAccessiblePosts(request))
                .build());
    }

    @GetMapping("/getPostByIdIfAccessible/{postId}")
    public ResponseEntity<?> getPostByIdIfAccessible(@PathVariable("postId") String postId, HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Success")
                .result(postService.getPostByIdIfAccessible(postId, request))
                .build());
    }

    @PostMapping("/createPost")
    public ResponseEntity<?> createPost(
            @ModelAttribute PostRequestDto postRequestDto,
            HttpServletRequest request
    ) {
        postService.createPost(postRequestDto, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Post created successfully")
                .build());
    }
}
