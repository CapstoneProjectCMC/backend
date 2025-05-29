package com.codecampus.identity.repository.account;

import com.codecampus.identity.entity.account.Token;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository
    extends JpaRepository<Token, String>
{
  Optional<Token> findByAccessToken(String accessToken);

  Optional<Token> findByRefreshToken(String refreshToken);

  @Query("SELECT t " +
      "FROM Token t " +
      "WHERE t.user.id = :userId AND t.revoked = false AND t.expired = false")
  List<Token> findAllValidTokensByUser(String userId);
}
