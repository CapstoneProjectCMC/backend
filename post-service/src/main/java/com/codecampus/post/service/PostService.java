package com.codecampus.post.service;


import com.codecampus.post.dto.common.PageResponse;
import com.codecampus.post.dto.request.AddFileDocumentDto;
import com.codecampus.post.dto.request.PostRequestDto;
import com.codecampus.post.dto.response.AddFileResponseDto;
import com.codecampus.post.dto.response.FileResult;
import com.codecampus.post.dto.response.PostResponseDto;
import com.codecampus.post.entity.Post;
import com.codecampus.post.exception.AppException;
import com.codecampus.post.exception.ErrorCode;
import com.codecampus.post.helper.AuthenticationHelper;
import com.codecampus.post.helper.PostHelper;
import com.codecampus.post.mapper.PostMapper;
import com.codecampus.post.repository.PostRepository;
import com.codecampus.post.repository.httpClient.FileServiceClient;
import com.codecampus.post.utils.PageResponseUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

      MultipartFile file = fileDoc.getFile();

      // Kiểm tra file được tải lên có phải ảnh thật hay không
      if (!isRealImage(file)) {
        throw new AppException(ErrorCode.INVALID_FILE_TYPE);
      }

      // Upload file sau khi xác thực
      AddFileResponseDto response = fileServiceClient.uploadFile(fileDoc);

      // Nếu API C# trả result là String URL
      fileUrls = Optional.ofNullable(response)
          .map(AddFileResponseDto::getResult)
          .map(FileResult::getUrl)
          .map(List::of)
          .orElse(Collections.emptyList());
    }

    Post post = postMapper.toPostFromPostRequestDto(postRequestDto);
    post.setImagesUrls(fileUrls);
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
    List<String> updatedImageUrls = new ArrayList<>();
    List<String> existingImages =
        Optional.ofNullable(existingPost.getImagesUrls())
            .orElse(Collections.emptyList());

    // 1. Giữ lại ảnh cũ mà người dùng vẫn muốn giữ
    if (postRequestDto.getOldImagesUrls() != null &&
        !postRequestDto.getOldImagesUrls().isEmpty()) {
      updatedImageUrls.addAll(
          existingPost.getImagesUrls().stream()
              .filter(postRequestDto.getOldImagesUrls()::contains)
              .toList()
      );
    }

    // 2. Nếu có ảnh mới thì kiểm tra và upload
    if (postRequestDto.getFileDocument() != null &&
        postRequestDto.getFileDocument().getFile() != null &&
        !postRequestDto.getFileDocument().getFile().isEmpty()) {

      MultipartFile file = postRequestDto.getFileDocument().getFile();
      if (!isRealImage(file)) {
        throw new AppException(ErrorCode.INVALID_FILE_TYPE);
      }

      // Upload ảnh mới
      AddFileResponseDto response = fileServiceClient.uploadFile(
          postRequestDto.getFileDocument());
      List<String> newFileUrls = Optional.ofNullable(response)
          .map(AddFileResponseDto::getResult)
          .map(FileResult::getUrl)
          .map(List::of)
          .orElse(Collections.emptyList());

      updatedImageUrls.addAll(newFileUrls);
    }

    // 3. Cập nhật các field khác
    postMapper.updatePostRequestDtoToPost(postRequestDto, existingPost);
    existingPost.setImagesUrls(updatedImageUrls);
    postRepository.save(existingPost);
  }


  public void deletePost(String postId) {
    String deletedBy = AuthenticationHelper.getMyUsername();
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    post.markDeleted(deletedBy);
    postRepository.save(post);
  }

  public boolean isRealImage(MultipartFile file) {
    try (InputStream input = file.getInputStream()) {
      return ImageIO.read(input) != null;
    } catch (IOException e) {
      return false;
    }
  }
}

