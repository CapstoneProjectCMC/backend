package com.codecampus.profile.repository;

import com.codecampus.profile.entity.Post;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository
    extends Neo4jRepository<Post, String> {

  Optional<Post> findByPostId(String postId);

  @Query("""
      MERGE (p:Post {postId: $postId})
      RETURN p
      """)
  Post mergeByPostId(String postId);

  @Query("""
      MERGE (p:Post {postId: $postId})
      ON CREATE SET p.title = $title
      ON MATCH  SET p.title = coalesce($title, p.title)
      RETURN p
      """)
  Post upsertPost(String postId, String title);

  // NEW: xóa theo postId (sẽ xóa cả các quan hệ SAVED_POST/REACTION/... tới Post đó)
  @Query("""
      MATCH (p:Post {postId: $postId})
      DETACH DELETE p
      """)
  void deleteByPostId(String postId);

  @Query("""
        MATCH (u:User {userId:$userId})-[:REACTION|:SAVED_POST]->(p:Post)
        RETURN count(DISTINCT p)
      """
  )
  long countPostsOfUser(String userId);

  @Query("""
        MATCH (:User {userId:$userId})-[r:REACTION]->(p:Post)
        WHERE r.type IN ['LIKE','LOVE']
        RETURN count(r)
      """)
  long countGoodReactions(String userId);
}
