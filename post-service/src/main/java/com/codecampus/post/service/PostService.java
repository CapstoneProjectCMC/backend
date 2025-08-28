package com.codecampus.post.service;


import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.common.PageResponse;
import com.codecampus.post.dto.request.AddFileDocumentDto;
import com.codecampus.post.dto.request.PostRequestDto;
import com.codecampus.post.dto.response.PostResponseDto;
import com.codecampus.post.dto.response.file.UploadedFileResponse;
import com.codecampus.post.entity.Post;
import com.codecampus.post.exception.AppException;
import com.codecampus.post.exception.ErrorCode;
import com.codecampus.post.helper.AuthenticationHelper;
import com.codecampus.post.helper.PostHelper;
import com.codecampus.post.mapper.PostMapper;
import com.codecampus.post.repository.PostRepository;
import com.codecampus.post.repository.httpClient.FileServiceClient;
import com.codecampus.post.utils.PageResponseUtils;
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

    List<String> fileUrls = Collections.emptyList();
    AddFileDocumentDto fileDoc = postRequestDto.getFileDocument();
    if (fileDoc != null && fileDoc.getFile() != null &&
        !fileDoc.getFile().isEmpty()) {

      // Upload file
      var api = fileServiceClient.uploadFile(fileDoc);

      // Nếu API C# trả result là String URL
      String uploadedUrl = Optional.ofNullable(api)
          .map(com.codecampus.post.dto.common.ApiResponse::getResult)
          .map(UploadedFileResponse::getUrl)
          .orElse(null);

      fileUrls =
          uploadedUrl != null ? List.of(uploadedUrl) : Collections.emptyList();
    }

    Post post = postMapper.toPostFromPostRequestDto(postRequestDto);
    post.setFileUrls(fileUrls);
    post.setUserId(userId);

    postRepository.save(post);
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

    // Nếu có file mới thì upload và append
    if (postRequestDto.getFileDocument() != null &&
        postRequestDto.getFileDocument().getFile() != null &&
        !postRequestDto.getFileDocument().getFile().isEmpty()) {

      var api = fileServiceClient
          .uploadFile(postRequestDto.getFileDocument());

      Optional.ofNullable(api)
          .map(ApiResponse::getResult)
          .map(UploadedFileResponse::getUrl)
          .ifPresent(updatedFileUrls::add);
    }

    // Cập nhật các field đơn khác
    postMapper.updatePostRequestDtoToPost(postRequestDto, existingPost);

    existingPost.setFileUrls(updatedFileUrls);
    postRepository.save(existingPost);
  }


  public void deletePost(String postId) {
    String deletedBy = AuthenticationHelper.getMyUsername();
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    post.markDeleted(deletedBy);
    postRepository.save(post);
  }
}

