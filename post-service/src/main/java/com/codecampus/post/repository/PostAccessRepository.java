package com.codecampus.post.repository;

import com.codecampus.post.entity.PostAccess;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostAccessRepository
    extends JpaRepository<PostAccess, String> {

  List<PostAccess> findByPost_PostId(String postId);

  Page<PostAccess> findByPost_PostId(
      String postId, Pageable pageable);

  List<PostAccess> findByPost_PostIdAndUserIdIn(
      String postId,
      List<String> userIds);
}

