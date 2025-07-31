package com.codecampus.profile.repository;

import com.codecampus.profile.entity.Post;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository
        extends Neo4jRepository<Post, String> {

    Optional<Post> findByPostId(String postId);

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
