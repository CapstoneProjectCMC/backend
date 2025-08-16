package com.codecampus.post.repository;

import com.codecampus.post.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, String> {

    List<PostComment> findByPost_PostIdAndParentCommentIsNullOrderByCommentIdDesc(String postId);

    List<PostComment> findByParentComment_CommentIdOrderByCommentIdAsc(String parentCommentId);
}

