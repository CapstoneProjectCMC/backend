package com.codecampus.post.service;


import com.codecampus.post.dto.common.PageResponse;
import com.codecampus.post.dto.request.PostRequestDto;
import com.codecampus.post.dto.response.PostResponseDto;
import com.codecampus.post.entity.Post;
import com.codecampus.post.exception.AppException;
import com.codecampus.post.exception.ErrorCode;
import com.codecampus.post.helper.AuthenticationHelper;
import com.codecampus.post.helper.PostHelper;
import com.codecampus.post.mapper.PostMapper;
import com.codecampus.post.repository.PostRepository;
import com.codecampus.post.repository.httpClient.FileServiceClient;
import com.codecampus.post.service.kafka.PostEventProducer;
import com.codecampus.post.utils.PageResponseUtils;
import dtos.PostSummary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostService {
  PostRepository postRepository;
  PostMapper postMapper;
  FileServiceClient fileServiceClient;
  PostHelper postHelper;
  PostEventProducer postEventProducer;

  public PageResponse<PostResponseDto> getAllMyPosts(
      int page, int size) {

    String userId = AuthenticationHelper.getMyUserId();

    Pageable pageable =
        PageRequest.of(Math.max(1, page) - 1, size,
            Sort.by("createdAt").descending());
    Page<PostResponseDto> postPage = postRepository
        .findByUserId(userId, pageable)
        .map(postHelper::toPostResponseDtoFromPost);

    return PageResponseUtils.toPageResponse(postPage, Math.max(1, page));
  }

  public PageResponse<PostResponseDto> getVisiblePosts(
      int page, int size) {

    String userId = AuthenticationHelper.getMyUserId();

    Pageable pageable =
        PageRequest.of(Math.max(1, page) - 1, size,
            Sort.by("createdAt").descending());

    Page<PostResponseDto> postPage = postRepository
        .findAllVisiblePosts(userId, pageable)
        .map(postHelper::toPostResponseDtoFromPost);

    return PageResponseUtils.toPageResponse(postPage, Math.max(1, page));
  }

  public PageResponse<PostResponseDto> searchVisiblePosts(
      String searchText,
      int page, int size) {
    String userId = AuthenticationHelper.getMyUserId();

    Pageable pageable =
        PageRequest.of(Math.max(1, page) - 1, size);

    Page<PostResponseDto> postPage = postRepository
        .searchVisiblePosts(searchText, userId, pageable)
        .map(postHelper::toPostResponseDtoFromPost);

    return PageResponseUtils.toPageResponse(postPage, Math.max(1, page));
  }

  public PostResponseDto getPostDetail(
      String postId) {
    String userId = AuthenticationHelper.getMyUserId();

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

    if (!postHelper.canView(post, userId)) {
      throw new AppException(ErrorCode.POST_NOT_AUTHORIZED);
    }

    return postHelper.toPostResponseDtoFromPost(post);
  }

  public void createPost(
      PostRequestDto postRequestDto) {
    String userId = AuthenticationHelper.getMyUserId();
    String orgId = AuthenticationHelper.getMyOrgId();

    List<String> fileUrls =
        postHelper.uploadAll(postRequestDto.getFileDocument());

    Post post = postMapper.toPostFromPostRequestDto(postRequestDto);
    post.setFileUrls(fileUrls);
    post.setUserId(userId);
    post.setOrgId(orgId);

    postRepository.save(post);

    postEventProducer.publishCreated(post);
  }

  public void updatePost(
      String postId,
      PostRequestDto postRequestDto) {
    String userId = AuthenticationHelper.getMyUserId();

    Post existingPost = postRepository.findById(postId)
        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

    // Chỉ cho phép người tạo hoặc admin chỉnh sửa
    if (!existingPost.getUserId().equals(userId) &&
        !AuthenticationHelper.getMyRoles().contains("ADMIN")) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    // Danh sách ảnh sau khi chỉnh sửa
    List<String> updatedFileUrls = new ArrayList<>(
        Optional.ofNullable(existingPost.getFileUrls())
            .orElse(Collections.emptyList())
    );

    List<String> newUrls = postHelper.uploadAll(
        postRequestDto.getFileDocument());
    if (newUrls != null && !newUrls.isEmpty()) {
      updatedFileUrls.addAll(newUrls);
    }

    // Cập nhật các field đơn khác
    postMapper.updatePostRequestDtoToPost(postRequestDto, existingPost);

    existingPost.setFileUrls(updatedFileUrls);
    postRepository.save(existingPost);

    postEventProducer.publishUpdated(existingPost);
  }


  public void deletePost(String postId) {
    String deletedBy = AuthenticationHelper.getMyUsername();
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    post.markDeleted(deletedBy);
    postRepository.save(post);

    postEventProducer.publishDeleted(post);
  }

  public PostSummary getPostSummary(String postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    return postMapper.toPostSummaryFromPost(post);
  }
}

