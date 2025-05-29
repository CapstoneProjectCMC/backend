package com.codecampus.identity.service.authentication;

import com.codecampus.identity.dto.request.authentication.RefreshTokenRequest;
import com.codecampus.identity.entity.account.Token;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.repository.account.TokenRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenService
{
  TokenRepository tokenRepository;

  @Transactional
  public Token createToken(
      User user,
      String accessToken,
      String refreshToken,
      Instant expiryTime)
  {
    return tokenRepository.save(Token.builder()
        .id(UUID.randomUUID().toString())
        .user(user)
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .expiryTime(expiryTime)
        .revoked(false)
        .expired(false)
        .build()
    );
  }

  @Transactional
  public void revokeToken(String tokenId)
  {
    tokenRepository.findById(tokenId)
        .ifPresent(token -> {
          token.setRevoked(true);
          tokenRepository.save(token);
        });
  }

  @Transactional
  public void revokeAllUserTokens(User user)
  {
    List<Token> validTokens = tokenRepository
        .findAllValidTokensByUser(user.getId());
    if (validTokens.isEmpty())
    {
      return;
    }

    validTokens.forEach(token -> {
      token.setRevoked(true);
      token.setExpired(true);
    });

    tokenRepository.saveAll(validTokens);
  }

  @Transactional
  public Token refreshAccessToken(
      RefreshTokenRequest request)
  {
    Token token = tokenRepository
        .findByRefreshToken(request.getRefreshToken())
        .orElseThrow(
            () -> new AppException((ErrorCode.INVALID_TOKEN))
        );

    if (token.isRevoked() || token.isExpired())
    {
      throw new AppException(ErrorCode.INVALID_TOKEN);
    }

    return token;
  }
}
