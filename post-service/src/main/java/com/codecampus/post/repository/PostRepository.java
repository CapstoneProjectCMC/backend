package com.codecampus.post.repository;

import com.codecampus.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {
    @Query("""
    SELECT DISTINCT p FROM Post p
    LEFT JOIN p.accesses a
    WHERE p.isPublic = true OR a.userId = :userId
""")
    List<Post> findAccessiblePosts(String userId);

    @Query("""
    SELECT p FROM Post p
    LEFT JOIN p.accesses a
    WHERE p.postId = :postId AND (p.isPublic = true OR a.userId = :userId)
""")
    Optional<Post> findAccessiblePostById(String postId, String userId);
}
