package com.codecampus.profile.repository;

import com.codecampus.profile.entity.FileResource;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FileResourceRepository
    extends Neo4jRepository<FileResource, String>
{
  Optional<FileResource> findByFileId(String fileId);
}
