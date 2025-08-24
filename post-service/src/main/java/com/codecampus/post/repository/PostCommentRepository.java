package com.codecampus.post.repository;

import com.codecampus.post.entity.PostComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentRepository
    extends JpaRepository<PostComment, String> {

  List<PostComment> findByPost_PostIdAndParentCommentIsNullOrderByCommentIdDesc(
      String postId);

  List<PostComment> findByParentComment_CommentIdOrderByCommentIdAsc(
      String parentCommentId);
}

