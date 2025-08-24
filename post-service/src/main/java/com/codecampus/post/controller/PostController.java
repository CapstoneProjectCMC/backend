package com.codecampus.post.controller;

import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.common.PageRequestDto;
import com.codecampus.post.dto.common.PageResponse;
import com.codecampus.post.dto.request.PostRequestDto;
import com.codecampus.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/posts")
public class PostController {

  @Autowired
  private PostService postService;

  @GetMapping("/getAllMyPosts")
  public ResponseEntity<PageResponse<?>> getMyPosts(
          HttpServletRequest request,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size) {

    PageResponse<?> response = postService.getAllPostsByUserId(request, page, size);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/getAllAccessiblePosts")
  public ResponseEntity<?> getAllAccessiblePosts(HttpServletRequest request,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(ApiResponse.builder()
        .message("Success")
        .result(postService.getAllAccessiblePosts(request, page, size))
        .build());
  }

  @GetMapping("/seachPosts/{searchText}")
  public ResponseEntity<?> SeachPosts(
      @PathVariable("searchText") String searchText,
      HttpServletRequest request,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(ApiResponse.builder()
        .message("Success")
        .result(postService.SeachPosts(searchText, request, page, size))
        .build());
  }

  @GetMapping("/getPostByIdIfAccessible/{postId}")
  public ResponseEntity<?> getPostByIdIfAccessible(
      @PathVariable("postId") String postId, HttpServletRequest request) {
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
  public ResponseEntity<?> deletePost(@PathVariable("postId") String postId,
                                      HttpServletRequest request) {
    postService.deletePost(postId, request);
    return ResponseEntity.ok(ApiResponse.builder()
        .message("Post deleted successfully")
        .build());
  }
}
