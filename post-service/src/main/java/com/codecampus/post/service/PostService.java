package com.codecampus.post.service;


import com.codecampus.post.config.CustomJwtDecoder;
import com.codecampus.post.dto.common.PageRequestDto;
import com.codecampus.post.dto.common.PageResponse;
import com.codecampus.post.dto.request.PostRequestDto;
import com.codecampus.post.dto.response.AddFileResponseDto;
import com.codecampus.post.dto.response.PostResponseDto;
import com.codecampus.post.dto.response.ProfileResponseDto;
import com.codecampus.post.entity.Post;
import com.codecampus.post.exception.AppException;
import com.codecampus.post.exception.ErrorCode;
import com.codecampus.post.mapper.PostMapper;
import com.codecampus.post.repository.PostRepository;
import com.codecampus.post.repository.httpClient.FileServiceClient;
import com.codecampus.post.repository.httpClient.ProfileServiceClient;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {
  private final CustomJwtDecoder customJwtDecoder;
  private final PostRepository postRepository;
  private final PostMapper postMapper;
  private final FileServiceClient fileServiceClient;
  private final ProfileServiceClient profileServiceClient;

  public PageResponse<?> getAllAccessiblePosts(HttpServletRequest request,
                                               PageRequestDto pageRequestDto) {
    String token = request.getHeader("Authorization");
    String userId = customJwtDecoder.decode(token.substring(7)).getClaims()
        .get("userId").toString();
    Pageable pageable = PageRequest.of(pageRequestDto.getPage(),
        pageRequestDto.getSize(), Sort.by("createdAt").descending());
    Page<Post> postPage =
        postRepository.findAllVisiblePosts(userId, pageable);

    // Map Post -> PostResponseDto
    List<PostResponseDto> postResponses = postPage.getContent().stream()
        .map(post -> {
          ProfileResponseDto profile = profileServiceClient
              .getUserProfileById(post.getUserId()).getResult();

          return PostResponseDto.builder()
              .postId(post.getPostId())
              .userId(post.getUserId())
              .username(profile.getUsername())
              .avatarUrl(profile.getAvatarUrl())
              .orgId(post.getOrgId())
              .postType(post.getPostType())
              .title(post.getTitle())
              .content(post.getContent())
              .allowComment(post.getAllowComment())
              .hashtag(post.getHashtag())
              .status(post.getStatus())
              .accesses(post.getAccesses())
              .createdAt(post.getCreatedAt().toString())
              .build();
        })
        .toList();

    return PageResponse.<PostResponseDto>builder()
        .currentPage(postPage.getNumber())
        .totalPages(postPage.getTotalPages())
        .pageSize(postPage.getSize())
        .totalElements(postPage.getTotalElements())
        .data(postResponses)
        .build();
  }

  public PageResponse<?> SeachPosts(String searchText,
                                    HttpServletRequest request,
                                    PageRequestDto pageRequestDto) {
    String token = request.getHeader("Authorization");
    String userId = customJwtDecoder.decode(token.substring(7)).getClaims()
        .get("userId").toString();
    Pageable pageable = PageRequest.of(pageRequestDto.getPage(),
        pageRequestDto.getSize(), Sort.by("created_at").descending());
    Page<Post> postPage =
        postRepository.searchVisiblePosts(searchText, userId, pageable);

    return PageResponse.<Post>builder()
        .currentPage(postPage.getNumber())
        .totalPages(postPage.getTotalPages())
        .pageSize(postPage.getSize())
        .totalElements(postPage.getTotalElements())
        .data(postPage.getContent())
        .build();
  }

  public PostResponseDto getPostByIdIfAccessible(String postId,
                                                 HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    String userId = customJwtDecoder.decode(token.substring(7))
        .getClaims()
        .get("userId").toString();

    Optional<Post> optionalPost = postRepository.findById(postId);
    if (optionalPost.isEmpty()) {
      throw new AppException(ErrorCode.POST_NOT_FOUND);
    }
    if (optionalPost.get().getAccesses().contains(userId)) {
      throw new AppException(ErrorCode.POST_NOT_AUTHORIZED);
    }

    ProfileResponseDto profile =
        profileServiceClient.getUserProfileById(optionalPost.get().getUserId())
            .getResult();

    return PostResponseDto.builder()
        .postId(optionalPost.get().getPostId())
        .userId(optionalPost.get().getUserId())
        .username(profile.getUsername())
        .avatarUrl(profile.getAvatarUrl())
        .orgId(optionalPost.get().getOrgId())
        .postType(optionalPost.get().getPostType())
        .title(optionalPost.get().getTitle())
        .content(optionalPost.get().getContent())
        .allowComment(optionalPost.get().getAllowComment())
        .hashtag(optionalPost.get().getHashtag())
        .status(optionalPost.get().getStatus())
        .accesses(optionalPost.get().getAccesses())
        .createdAt(optionalPost.get().getCreatedAt().toString())
        .build();
  }

  public void createPost(PostRequestDto postRequestDto,
                         HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    String userId = customJwtDecoder.decode(token.substring(7))
        .getClaims()
        .get("userId")
        .toString();

    List<String> fileUrls = Collections.emptyList();

    var fileDoc = postRequestDto.getFileDocument();
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
          .map(List::of)
          .orElse(Collections.emptyList());
    }

    Post post = postMapper.toPostFromPostRequestDto(postRequestDto);
    post.setImagesUrls(fileUrls);
    post.setUserId(userId);

    postRepository.save(post);
  }

  public void updatePost(PostRequestDto postRequestDto,
                         HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    String userId = customJwtDecoder.decode(token.substring(7))
        .getClaims()
        .get("userId").toString();
    String username = customJwtDecoder.decode(token.substring(7))
        .getClaims()
        .get("username").toString();

    Optional<Post> optionalPost =
        postRepository.findById(postRequestDto.getPostId());
    if (optionalPost.isEmpty()) {
      throw new AppException(ErrorCode.POST_NOT_FOUND);
    }

    Post existingPost = optionalPost.get();

    // Chỉ cho phép người tạo hoặc admin chỉnh sửa
    if (!existingPost.getUserId().equals(userId) &&
        !"admin".equals(username)) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    // Danh sách ảnh sau khi chỉnh sửa
    List<String> updatedImageUrls = new ArrayList<>();

    // 1. Giữ lại ảnh cũ mà người dùng vẫn muốn giữ
    if (postRequestDto.getOldImgesUrls() != null &&
        !postRequestDto.getOldImgesUrls().isEmpty()) {
      updatedImageUrls.addAll(
          existingPost.getImagesUrls().stream()
              .filter(url -> postRequestDto.getOldImgesUrls()
                  .contains(url))
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
          .map(List::of)
          .orElse(Collections.emptyList());

      updatedImageUrls.addAll(newFileUrls);
    }

    // 3. Cập nhật các field khác
    postMapper.updatePostRequestDtoToPost(postRequestDto, existingPost);
    existingPost.setImagesUrls(updatedImageUrls);

    postRepository.save(existingPost);
  }


  public void deletePost(String postId, HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    String deletedBy =
        customJwtDecoder.decode(token.substring(7)).getClaims()
            .get("username").toString();
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Post not found with id: " + postId));
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

