package com.codecampus.post.service;

import com.codecampus.post.config.CustomJwtDecoder;
import com.codecampus.post.dto.request.CommentRequestDto;
import com.codecampus.post.dto.request.UpdateCommentDto;
import com.codecampus.post.dto.response.CommentResponseDto;
import com.codecampus.post.dto.response.ProfileResponseDto;
import com.codecampus.post.entity.Post;
import com.codecampus.post.entity.PostComment;
import com.codecampus.post.repository.PostCommentRepository;
import com.codecampus.post.repository.PostRepository;
import com.codecampus.post.repository.httpClient.ProfileServiceClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommentService {

  private final ProfileServiceClient profileServiceClient;
  private final PostCommentRepository postCommentRepository;
  private final PostRepository postRepository;
  private final CustomJwtDecoder customJwtDecoder;

  @Transactional
  public PostComment addComment(CommentRequestDto dto,
                                HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token == null || !token.startsWith("Bearer ")) {
      throw new IllegalArgumentException(
          "Authorization token is missing or invalid");
    }
    String userId =
        customJwtDecoder.decode(token.substring(7)).getClaims().get("userId")
            .toString(); // Assuming user ID is stored in the principal

    Post post = postRepository.findById(dto.getPostId())
        .orElseThrow(() -> new RuntimeException("Post not found"));

    PostComment comment = new PostComment();
    comment.setPost(post);
    comment.setUserId(userId);
    comment.setContent(dto.getContent());

    if (dto.getParentCommentId() != null) {
      PostComment parent =
          postCommentRepository.findById(dto.getParentCommentId())
              .orElseThrow(
                  () -> new RuntimeException("Parent comment not found"));
      comment.setParentComment(parent);
    }

    return postCommentRepository.save(comment);
  }

  public List<CommentResponseDto> getCommentsByPost(String postId) {
    List<PostComment> parents = postCommentRepository
        .findByPost_PostIdAndParentCommentIsNullOrderByCommentIdDesc(postId);

    return parents.stream()
        .filter(c -> !c.isDeleted()) // chỉ lấy comment chưa xóa
        .map(c -> mapWithLimitDepth(c, 1)) // bắt đầu từ depth = 1
        .toList();
  }

  private CommentResponseDto mapWithLimitDepth(PostComment comment, int depth) {
    List<CommentResponseDto> replyDtos;

    if (depth < 2) {
      replyDtos = comment.getReplies() != null
          ? comment.getReplies().stream()
          .filter(r -> !r.isDeleted())
          .flatMap(r -> {
            if (depth + 1 < 2) {
              return Stream.of(mapWithLimitDepth(r, depth + 1));
            } else {
              return flattenReplies(r).stream();
            }
          })
          .toList()
          : List.of();
    } else {
      replyDtos = List.of();
    }

    // Gọi profile service để lấy thông tin user
    ProfileResponseDto userProfile =
        profileServiceClient.getUserProfileById(comment.getUserId())
            .getResult();

    return new CommentResponseDto(
        comment.getCommentId(),
        comment.getParentComment() != null ?
            comment.getParentComment().getCommentId() : null,
        comment.getContent(),
        replyDtos,
        userProfile
    );
  }

  /**
   * Flatten toàn bộ reply từ comment con (cấp >= 2) thành danh sách cấp 2
   */
  private List<CommentResponseDto> flattenReplies(PostComment comment) {
    List<CommentResponseDto> flatList = new ArrayList<>();

    if (!comment.isDeleted()) {
      ProfileResponseDto userProfile =
          profileServiceClient.getUserProfileById(comment.getUserId())
              .getResult();

      flatList.add(new CommentResponseDto(
          comment.getCommentId(),
          comment.getParentComment() != null ?
              comment.getParentComment().getCommentId() : null,
          comment.getContent(),
          List.of(),
          userProfile
      ));
    }

    if (comment.getReplies() != null) {
      for (PostComment child : comment.getReplies()) {
        flatList.addAll(flattenReplies(child));
      }
    }

    return flatList;
  }

  @Transactional
  public void updateComment(UpdateCommentDto requestDto,
                            HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token == null || !token.startsWith("Bearer ")) {
      throw new IllegalArgumentException(
          "Authorization token is missing or invalid");
    }
    String userId =
        customJwtDecoder.decode(token.substring(7)).getClaims().get("userId")
            .toString(); // Assuming user ID is stored in the principal
    if (userId == null) {
      throw new IllegalArgumentException(
          "User ID is required for updating comment");
    }

    PostComment comment =
        postCommentRepository.findById(requestDto.getCommentId())
            .orElseThrow(() -> new RuntimeException("Comment not found"));

    if (!comment.getUserId().equals(userId)) {
      throw new RuntimeException("Not allowed to edit this comment");
    }

    comment.setContent(requestDto.getContent());
    postCommentRepository.save(comment);
  }

  @Transactional
  public void deleteComment(String commentId, HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token == null || !token.startsWith("Bearer ")) {
      throw new IllegalArgumentException(
          "Authorization token is missing or invalid");
    }
    String userId =
        customJwtDecoder.decode(token.substring(7)).getClaims().get("userId")
            .toString();
    if (userId == null) {
      throw new IllegalArgumentException(
          "User ID is required for deleting comment");
    }

    PostComment comment = postCommentRepository.findById(commentId)
        .orElseThrow(() -> new RuntimeException("Comment not found"));

    if (!comment.isDeleted()) {
      comment.markDeleted(userId);
      postCommentRepository.save(comment);
    }
  }
}

