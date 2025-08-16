package com.codecampus.post.repository;

import com.codecampus.post.entity.PostAccess;
import com.codecampus.post.entity.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, String> {
    Optional<PostReaction> findByPostIdAndUserId(String postId, String userId);
    Optional<PostReaction> findByPostIdAndUserIdAndCommentId(String postId, String userId, String commentId);
}
