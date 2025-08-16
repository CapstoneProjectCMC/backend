package com.codecampus.post.repository;

import com.codecampus.post.entity.PostAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostAccessRepository extends JpaRepository<PostAccess, String> {

    List<PostAccess> findByPostId(String postId);

    void deleteByPostIdAndUserIdIn(String postId, List<String> userIds);
}

