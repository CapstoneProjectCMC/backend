package com.codecampus.profile.repository;

import com.codecampus.profile.entity.Post;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository
    extends Neo4jRepository<Post, String>
{

  Optional<Post> findByPostId(String postId);

  @Query("""
        MATCH (p:Post)<-[:CREATED_EXERCISE|:SAVED_POST]-(:User {userId:$userId})
        RETURN count(p)
      """)
  long countPostsOfUser(String userId);

  @Query("""
        MATCH (:User {userId:$userId})-[r:REACTION]->(p:Post)
        WHERE r.type IN ['LIKE','LOVE']
        RETURN count(r)
      """)
  long countGoodReactions(String userId);
}
