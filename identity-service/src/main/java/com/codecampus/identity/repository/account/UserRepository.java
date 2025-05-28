package com.codecampus.identity.repository.account;

import com.codecampus.identity.entity.account.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, String> {
    Optional<User> findByUsernameOrEmail(
            String userName,
            String email);

    boolean existsByUsernameOrEmail(
            String username,
            String email);
}
