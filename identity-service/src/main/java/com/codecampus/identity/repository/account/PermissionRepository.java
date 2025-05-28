package com.codecampus.identity.repository.account;

import com.codecampus.identity.entity.account.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository
        extends JpaRepository<Permission, String> {
    Optional<Permission> findByName(String name);
}
