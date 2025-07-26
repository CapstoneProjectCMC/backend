package com.codecampus.profile.repository;

import com.codecampus.profile.entity.PackageEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface PackageRepository
        extends Neo4jRepository<PackageEntity, String> {
    Optional<PackageEntity> findByPackageId(String packageId);

}
