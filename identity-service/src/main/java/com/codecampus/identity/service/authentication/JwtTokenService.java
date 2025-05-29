package com.codecampus.identity.service.authentication;

import java.security.KeyPair;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtTokenService
{

  KeyPair keyPair;
  JwtEncoder jwtEncoder;
  JwtDecoder jwtDecoder;

  @Value("${app.jwt.expiration}")
  @NonFinal
  int jwtExpiration;

  @Value("${app.jwt.refresh-expiration}")
  @NonFinal
  int jwtRefreshExpiration;

  @Value("${app.jwt.key-secret}")
  @NonFinal
  String jwtKeySecret;

  public String generateAccessToken(
      UserDetails userDetails)
  {
    return buildToken(
        userDetails,
        jwtExpiration,
        ChronoUnit.DAYS);
  }

  public String generateRefreshToken(
      UserDetails userDetails)
  {
    return buildToken(
        userDetails,
        jwtRefreshExpiration,
        ChronoUnit.DAYS);
  }

  public String buildToken(
      UserDetails userDetails,
      long expiration,
      ChronoUnit unit)
  {
    Instant now = Instant.now();
    String tokenId = UUID.randomUUID().toString();

    JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
        .issuer("Code Campus")
        .issuedAt(now)
        .expiresAt(now.plus(expiration, unit))
        .id(tokenId)
        .claim("permissions", userDetails
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList())
        )
        .build();

    JwsHeader jwsHeader = JwsHeader
        .with(SignatureAlgorithm.RS256)
        .keyId(jwtKeySecret)
        .build();

    return jwtEncoder
        .encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet))
        .getTokenValue();
  }

  public Instant getExpirationFromToken(String token)
  {
    Jwt jwt = jwtDecoder.decode(token);
    return jwt.getExpiresAt();
  }
}
