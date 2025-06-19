package com.codecampus.identity.repository.account;

import com.codecampus.identity.entity.account.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
    extends JpaRepository<User, String> {
  Optional<User> findByUsernameOrEmail(
      String userName,
      String email);

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  boolean existsByUsernameOrEmail(
      String username,
      String email);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
