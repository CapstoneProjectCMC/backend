package com.codecampus.post.repository;

import com.codecampus.post.entity.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentRepository
    extends JpaRepository<PostComment, String> {

  @Query("""
          SELECT c FROM PostComment c
          WHERE c.post.postId = :postId
            AND c.parentComment IS NULL
            AND c.deletedAt IS NULL
          ORDER BY c.createdAt DESC
      """)
  Page<PostComment> findTopLevelComments(
      @Param("postId") String postId,
      Pageable pageable);

  @Query("""
          SELECT c FROM PostComment c
          WHERE c.parentComment.commentId = :parentId
            AND c.deletedAt IS NULL
          ORDER BY c.createdAt ASC
      """)
  Page<PostComment> findReplies(
      @Param("parentId") String parentId,
      Pageable pageable);

  long countByPost_PostIdAndDeletedAtIsNull(
      String postId);
}

