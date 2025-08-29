package com.codecampus.organization.repository;

import com.codecampus.organization.entity.Organization;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository
    extends JpaRepository<Organization, String> {
  Optional<Organization> findByName(String name);
}