package com.codecampus.identity.repository.account;

import com.codecampus.identity.entity.account.PasswordResetToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository
    extends JpaRepository<PasswordResetToken, String> {
  Optional<PasswordResetToken> findByEmail(String email);

  void deleteByEmail(String email);
}
