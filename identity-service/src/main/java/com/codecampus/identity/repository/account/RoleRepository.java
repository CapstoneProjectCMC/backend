package com.codecampus.identity.repository.account;

import com.codecampus.identity.entity.account.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository
    extends JpaRepository<Role, String>
{
    Optional<Role> findByName(String roleName);
}
