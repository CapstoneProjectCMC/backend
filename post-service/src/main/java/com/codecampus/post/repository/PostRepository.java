package com.codecampus.post.repository;

import com.codecampus.post.entity.Post;
import feign.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {
  @Query("""
          SELECT DISTINCT p FROM Post p
          LEFT JOIN p.accesses a
          WHERE p.isPublic = true OR a.userId = :userId
      """)
  List<Post> findAccessiblePosts(String userId);

  Optional<Post> findPostByPostId(String postId);

  @Query("""
          SELECT DISTINCT p FROM Post p
          LEFT JOIN p.accesses pa
          WHERE p.deletedAt IS NULL
            AND (
                 p.postType = 'Global'
                 OR p.isPublic = true
                 OR (pa.userId = :userId AND (pa.isExcluded IS NULL OR pa.isExcluded = false))
            )
      """)
  Page<Post> findAllVisiblePosts(String userId, Pageable pageable);

  @Query(value = """
      SELECT DISTINCT p.* 
      FROM post p
      LEFT JOIN post_access pa ON p.post_id = pa.post_id
      WHERE p.deleted_at IS NULL
        AND p.search_vector @@ plainto_tsquery('simple', :searchText)
        AND (
             p.post_type = 'Global'
             OR p.is_public = true
             OR (pa.user_id = :userId AND (pa.is_excluded IS NULL OR pa.is_excluded = false))
        )
      """,
      nativeQuery = true)
  Page<Post> searchVisiblePosts(
      @Param("searchText") String searchText,
      @Param("userId") String userId,
      Pageable pageable
  );

  Page<Post> findByUserId(String userId, Pageable pageable);
}
