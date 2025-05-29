package com.codecampus.identity.repository.account;

import com.codecampus.identity.entity.account.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
    extends JpaRepository<User, String>
{
  Optional<User> findByUsernameOrEmail(
      String userName,
      String email);

  Optional<User> findByUsername(String username);

  boolean existsByUsernameOrEmail(
      String username,
      String email);
}
