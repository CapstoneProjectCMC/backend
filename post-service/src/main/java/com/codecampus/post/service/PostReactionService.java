package com.codecampus.post.service;

import com.codecampus.post.entity.Post;
import com.codecampus.post.entity.PostReaction;
import com.codecampus.post.exception.AppException;
import com.codecampus.post.exception.ErrorCode;
import com.codecampus.post.helper.AuthenticationHelper;
import com.codecampus.post.helper.PostHelper;
import com.codecampus.post.repository.PostReactionRepository;
import com.codecampus.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostReactionService {
  PostReactionRepository postReactionRepository;
  PostRepository postRepository;
  PostHelper postHelper;

  @Transactional
  public void togglePostReaction(String postId, String emoji) {

    String userId = AuthenticationHelper.getMyUserId();
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    if (!postHelper.canView(post, userId)) {
      throw new AppException(ErrorCode.POST_NOT_AUTHORIZED);
    }

    List<PostReaction> actives =
        postReactionRepository.findByPost_PostIdAndUserIdAndCommentIdIsNull(
            postId, userId);

    // Nếu lỡ có >1, giữ bản mới nhất, xoá mềm phần dư
    PostReaction current = null;
    for (int i = 0; i < actives.size(); i++) {
      PostReaction r = actives.get(i);
      if (i == 0) {
        current = r;
      } else {
        r.markDeleted(AuthenticationHelper.getMyUsername());
        postReactionRepository.save(r);
      }
    }

    // --- toggle ---
    if (current != null) {
      if (emoji.equals(current.getEmojiType())) {
        current.markDeleted(AuthenticationHelper.getMyUsername());
      } else {
        current.setEmojiType(emoji);
      }
      postReactionRepository.save(current);
      return;
    }

    // chưa từng react
    PostReaction newR = PostReaction.builder()
        .post(post)
        .userId(userId)
        .emojiType(emoji)
        .build();
    postReactionRepository.save(newR);
  }

  @Transactional
  public void toggleCommentReaction(String postId,
                                    String commentId,
                                    String emoji) {

    String userId = AuthenticationHelper.getMyUserId();
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    if (!postHelper.canView(post, userId)) {
      throw new AppException(ErrorCode.POST_NOT_AUTHORIZED);
    }

    List<PostReaction> actives =
        postReactionRepository.findByPost_PostIdAndUserIdAndCommentId(
            postId, userId, commentId);

    // Giữ 1 bản – xoá mềm phần dư
    PostReaction current = null;
    for (int i = 0; i < actives.size(); i++) {
      PostReaction r = actives.get(i);
      if (i == 0) {
        current = r;
      } else {
        r.markDeleted(AuthenticationHelper.getMyUsername());
        postReactionRepository.save(r);
      }
    }

    if (current != null) {
      if (emoji.equals(current.getEmojiType())) {
        current.markDeleted(AuthenticationHelper.getMyUsername()); // toggle off
      } else {
        current.setEmojiType(emoji);                               // đổi emoji
      }
      postReactionRepository.save(current);
      return;
    }

    // Chưa từng react
    PostReaction newR = PostReaction.builder()
        .post(post)
        .userId(userId)
        .commentId(commentId)
        .emojiType(emoji)
        .build();
    postReactionRepository.save(newR);
  }

  @Transactional
  public Map<String, Long> getReactionCount(String postId, String commentId) {
    long upvotes, downvotes;
    if (commentId == null) {
      upvotes = postReactionRepository.countByPost_PostIdAndEmojiType(postId,
          "upvote");
      downvotes = postReactionRepository.countByPost_PostIdAndEmojiType(postId,
          "downvote");
    } else {
      upvotes =
          postReactionRepository.countByPost_PostIdAndCommentIdAndEmojiType(
              postId, commentId, "upvote");
      downvotes =
          postReactionRepository.countByPost_PostIdAndCommentIdAndEmojiType(
              postId, commentId, "downvote");
    }
    Map<String, Long> result = new HashMap<>();
    result.put("upvote", upvotes);
    result.put("downvote", downvotes);
    return result;
  }
}
