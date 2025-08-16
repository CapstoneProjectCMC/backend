package com.codecampus.post.controller;

import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.common.PageRequestDto;
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
    public ResponseEntity<?> getAllAccessiblePosts(HttpServletRequest request, @RequestBody PageRequestDto pageRequestDto) {
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Success")
                .result(postService.getAllAccessiblePosts(request, pageRequestDto))
                .build());
    }

    @GetMapping("/seachPosts/{searchText}")
    public ResponseEntity<?> SeachPosts(@PathVariable("searchText") String searchText, HttpServletRequest request, @RequestBody PageRequestDto pageRequestDto) {
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Success")
                .result(postService.SeachPosts(searchText, request, pageRequestDto))
                .build());
    }

//    @GetMapping("/getPostByIdIfAccessible/{postId}")
//    public ResponseEntity<?> getPostByIdIfAccessible(@PathVariable("postId") String postId, HttpServletRequest request) {
//        return ResponseEntity.ok(ApiResponse.builder()
//                .message("Success")
//                .result(postService.getPostByIdIfAccessible(postId, request))
//                .build());
//    }

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

    @PutMapping("/updatePost")
    public ResponseEntity<?> updatePost(
            @ModelAttribute PostRequestDto postRequestDto,
            HttpServletRequest request
    ) {
        postService.updatePost(postRequestDto, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Post updated successfully")
                .build());
    }

    @PutMapping("/deletePost/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") String postId, HttpServletRequest request) {
        postService.deletePost(postId, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Post deleted successfully")
                .build());
    }
}
