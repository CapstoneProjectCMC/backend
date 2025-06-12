package com.codecampus.profile.repository;

import com.codecampus.profile.entity.UserProfile;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository
    extends Neo4jRepository<UserProfile, String>
{
  Optional<UserProfile> findByUserId(String userId);

  boolean existsByUserId(String userId);
}
