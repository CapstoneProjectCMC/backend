package com.codecampus.identity.service.authentication;

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
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtTokenService {

    KeyPair keyPair;
    JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration}")
    @NonFinal
    int jwtExpiration;

    @Value("${app.jwt.key-secret}")
    @NonFinal
    String jwtKeySecret;

    public String generateToken(
            UserDetails userDetails) {
        Instant now = Instant.now();

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("Code Campus")
                .issuedAt(now)
                .expiresAt(now.plus(3, ChronoUnit.DAYS))
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
}
