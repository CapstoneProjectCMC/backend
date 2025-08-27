package com.codecampus.post.repository;

import com.codecampus.post.entity.PostReaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReactionRepository
    extends JpaRepository<PostReaction, String> {

  long countByPost_PostIdAndEmojiType(
      String postId,
      String emojiType);

  long countByPost_PostIdAndCommentIdAndEmojiType(
      String postId,
      String commentId,
      String emojiType);

  List<PostReaction> findByPost_PostIdAndUserIdAndCommentIdIsNull(
      String postId, String userId);

  List<PostReaction> findByPost_PostIdAndUserIdAndCommentId(
      String postId, String userId, String commentId);
}
