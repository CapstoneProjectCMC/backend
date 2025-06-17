package com.codecampus.profile.repository;

import com.codecampus.profile.entity.Post;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository
    extends Neo4jRepository<Post, String>
{
  Optional<Post> findByPostId(String postId);
}
