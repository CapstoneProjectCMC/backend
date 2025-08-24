package com.codecampus.post.repository;

import com.codecampus.post.entity.PostReaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReactionRepository
    extends JpaRepository<PostReaction, String> {
  Optional<PostReaction> findByPost_PostIdAndUserId(String postId,
                                                    String userId);

  Optional<PostReaction> findByPost_PostIdAndUserIdAndCommentId(String postId,
                                                                String userId,
                                                                String commentId);

  long countByPost_PostIdAndEmojiType(String postId, String emojiType);

  long countByPost_PostIdAndCommentIdAndEmojiType(String postId,
                                                  String commentId,
                                                  String emojiType);
}
