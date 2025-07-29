package com.codecampus.profile.repository;

import com.codecampus.profile.entity.FileResource;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface FileResourceRepository
        extends Neo4jRepository<FileResource, String> {
    Optional<FileResource> findByFileId(String fileId);
}
