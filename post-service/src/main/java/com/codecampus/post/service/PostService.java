package com.codecampus.post.service;


import com.codecampus.post.Mapper.PostMapper;
import com.codecampus.post.config.CustomJwtDecoder;
import com.codecampus.post.dto.request.AddFileDocumentDto;
import com.codecampus.post.dto.request.PostRequestDto;
import com.codecampus.post.dto.response.AddFileResponseDto;
import com.codecampus.post.entity.Post;
import com.codecampus.post.exception.AppException;
import com.codecampus.post.exception.ErrorCode;
import com.codecampus.post.repository.PostRepository;
import com.codecampus.post.service.FeignConfig.FileServiceClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.net.http.HttpHeaders;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final CustomJwtDecoder customJwtDecoder;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final FileServiceClient fileServiceClient;

    public List<PostRequestDto> getAllAccessiblePosts(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String userId = customJwtDecoder.decode(token.substring(7)).getClaims().get("userId").toString();
        List<Post> posts = postRepository.findAccessiblePosts(userId).stream()
                .filter(post -> !post.isDeleted())
                .toList();
        return postMapper.toDtoList(posts);
    }

    public Optional<PostRequestDto> getPostByIdIfAccessible(String postId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String userId = customJwtDecoder.decode(token.substring(7)).getClaims().get("userId").toString();
        return postRepository.findAccessiblePostById(postId, userId)
                .filter(post -> !post.isDeleted())
                .map(postMapper::toDto);
    }

    public void createPost(PostRequestDto postRequestDto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String userId = customJwtDecoder.decode(token
                .substring(7))
                .getClaims().get("userId")
                .toString();

        List<String> fileUrls = Collections.emptyList();
        var fileDoc = postRequestDto.getFileDocument();
        if (fileDoc != null && fileDoc.getFile() != null && !fileDoc.getFile().isEmpty()) {
            AddFileResponseDto response = fileServiceClient.uploadFile(fileDoc);

            fileUrls = Optional.ofNullable(response)
                    .map(AddFileResponseDto::getResult)
                    .map(AddFileResponseDto.Result::getDatas)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(AddFileResponseDto.DataItem::getPresignedUrl)
                    .collect(Collectors.toList());
        }

        Post post = postMapper.toEntity(postRequestDto);
        post.setImagesUrls(fileUrls);
        post.setUserId(userId);

        postRepository.save(post);
    }

    public void updatePost(PostRequestDto postRequestDto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String userId = customJwtDecoder.decode(token.substring(7)).getClaims().get("userId").toString();

        Optional<Post> optionalPost = postRepository.findById(postRequestDto.getPostId());

        if (optionalPost.isEmpty()) throw new AppException(ErrorCode.POST_NOT_FOUND);

        Post existingPost = optionalPost.get();

        // Chỉ cho phép người tạo bài viết chỉnh sửa
        if (!existingPost.getUserId().equals(userId)) throw new AppException(ErrorCode.UNAUTHORIZED);

        postMapper.updateEntityFromDto(postRequestDto, existingPost);

        postRepository.save(existingPost);
    }

    public void deletePost(String postId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String deletedBy = customJwtDecoder.decode(token.substring(7)).getClaims().get("username").toString();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + postId));
        post.markDeleted(deletedBy);
        postRepository.save(post);
    }

}

