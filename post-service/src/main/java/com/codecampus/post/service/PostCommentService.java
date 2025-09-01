package com.codecampus.post.service;

import com.codecampus.post.dto.common.PageResponse;
import com.codecampus.post.dto.response.CommentCreatedDto;
import com.codecampus.post.dto.response.CommentResponseDto;
import com.codecampus.post.entity.Post;
import com.codecampus.post.entity.PostComment;
import com.codecampus.post.exception.AppException;
import com.codecampus.post.exception.ErrorCode;
import com.codecampus.post.helper.AuthenticationHelper;
import com.codecampus.post.helper.CommentHelper;
import com.codecampus.post.helper.PostHelper;
import com.codecampus.post.mapper.CommentMapper;
import com.codecampus.post.repository.PostCommentRepository;
import com.codecampus.post.repository.PostRepository;
import com.codecampus.post.service.cache.UserBulkLoader;
import com.codecampus.post.utils.PageResponseUtils;
import dtos.UserSummary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostCommentService {

  PostCommentRepository postCommentRepository;
  PostRepository postRepository;
  CommentMapper commentMapper;
  UserBulkLoader userBulkLoader;
  PostHelper postHelper;
  CommentHelper commentHelper;
  WebsocketRealtimeService realtime;

  @Transactional
  public CommentCreatedDto addTopLevelComment(
      String postId,
      String content) {
    String userId = AuthenticationHelper.getMyUserId();

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    if (!postHelper.canView(post, userId)) {
      throw new AppException(ErrorCode.POST_NOT_AUTHORIZED);
    }

    PostComment comment = new PostComment();
    comment.setPost(post);
    comment.setUserId(userId);
    comment.setContent(content);

    PostComment saved = postCommentRepository.save(comment);
    realtime.commentCreated(postId, commentHelper.toCommentResponseDto(saved));

    return commentMapper.toCreatedDtoFromPostComment(saved);
  }


  @Transactional
  public CommentCreatedDto addReply(
      String postId,
      String parentCommentId,
      String content) {
    String userId = AuthenticationHelper.getMyUserId();

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    if (!postHelper.canView(post, userId)) {
      throw new AppException(ErrorCode.POST_NOT_AUTHORIZED);
    }

    PostComment parent = postCommentRepository.findById(parentCommentId)
        .orElseThrow(
            () -> new AppException(ErrorCode.PARENT_COMMENT_NOT_FOUND));

    PostComment comment = new PostComment();
    comment.setPost(post);
    comment.setUserId(userId);
    comment.setContent(content);
    comment.setParentComment(parent);

    PostComment saved = postCommentRepository.save(comment);
    realtime.commentCreated(postId, commentHelper.toCommentResponseDto(saved));

    return commentMapper.toCreatedDtoFromPostComment(saved);
  }

  @Transactional(readOnly = true)
  public PageResponse<CommentResponseDto> getCommentsByPost(
      String postId, int page, int size, int replySize) {

    // 1) Lấy page comment gốc
    Page<PostComment> parents =
        postCommentRepository.findTopLevelComments(
            postId,
            PageRequest.of(Math.max(1, page) - 1, size));

    // 2) Preload reply & gom userId
    Map<String, List<PostComment>> repliesMap = new HashMap<>();
    Set<String> userIds = new HashSet<>();

    parents.forEach(parent -> {
      userIds.add(parent.getUserId());

      Page<PostComment> replyPage = postCommentRepository.findReplies(
          parent.getCommentId(),
          PageRequest.of(0, replySize <= 0 ? Integer.MAX_VALUE : replySize));

      List<PostComment> replies = replyPage.getContent();
      repliesMap.put(parent.getCommentId(), replies);
      replies.forEach(r -> userIds.add(r.getUserId()));
    });

    // 3) Bulk load user
    Map<String, UserSummary> userMap = userBulkLoader.loadAll(userIds);

    // 4) Mapping
    Page<CommentResponseDto> dtoPage = parents.map(parent -> {
      List<CommentResponseDto> replyDtos =
          repliesMap.getOrDefault(parent.getCommentId(), List.of())
              .stream()
              .map(r -> commentHelper.toCommentResponseDto(r, userMap))
              .toList();

      return commentHelper.toCommentResponseDto(parent, userMap, replyDtos);
    });

    return PageResponseUtils.toPageResponse(dtoPage, Math.max(1, page));
  }


  @Transactional
  public void updateComment(String commentId, String newContent) {
    String userId = AuthenticationHelper.getMyUserId();
    PostComment comment = postCommentRepository.findById(commentId)
        .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

    if (!comment.getUserId().equals(userId)) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }
    comment.setContent(newContent);
    PostComment saved = postCommentRepository.save(comment);

    realtime.commentUpdated(saved.getPost().getPostId(),
        commentHelper.toCommentResponseDto(saved));
  }

  @Transactional
  public void deleteComment(String commentId) {
    String by = AuthenticationHelper.getMyUsername();
    PostComment comment = postCommentRepository.findById(commentId)
        .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    if (!comment.isDeleted()) {
      comment.markDeleted(by);
      postCommentRepository.save(comment);
    }

    realtime.commentDeleted(comment.getPost().getPostId(),
        comment.getCommentId());
  }

  @Transactional(readOnly = true)
  public long countByPost(String postId) {
    return postCommentRepository
        .countByPost_PostIdAndDeletedAtIsNull(postId);
  }
}

