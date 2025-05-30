package com.codecampus.identity.service.authentication;

import com.codecampus.identity.dto.request.authentication.AuthenticationRequest;
import com.codecampus.identity.dto.request.authentication.IntrospectRequest;
import com.codecampus.identity.dto.request.authentication.LogoutRequest;
import com.codecampus.identity.dto.request.authentication.RefreshRequest;
import com.codecampus.identity.dto.response.authentication.AuthenticationResponse;
import com.codecampus.identity.dto.response.authentication.IntrospectResponse;
import com.codecampus.identity.entity.account.InvalidatedToken;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.repository.account.InvalidatedTokenRepository;
import com.codecampus.identity.repository.account.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;
import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService
{
  UserRepository userRepository;
  InvalidatedTokenRepository invalidatedTokenRepository;

  @NonFinal
  @Value("${app.jwt.signerKey}")
  protected String SIGNER_KEY;

  @NonFinal
  @Value("${app.jwt.valid-duration}")
  protected long VALID_DURATION;

  @NonFinal
  @Value("${app.jwt.refreshable-duration}")
  protected long REFRESH_DURATION;

  public IntrospectResponse introspect(
      IntrospectRequest request)
      throws JOSEException, ParseException
  {
    var token = request.getToken();
    boolean isValid = true;

    try
    {
      verifyToken(token, false);
    } catch (AppException e)
    {
      isValid = false;
    }

    return IntrospectResponse.builder()
        .valid(isValid)
        .build();
  }

  public AuthenticationResponse authenticate(
      AuthenticationRequest request)
  {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    var user = userRepository
        .findByUsername(request.getUsername())
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

    if (!authenticated)
    {
      throw new AppException(ErrorCode.INVALID_CREDENTIALS);
    }

    var token = generateToken(user);

    return AuthenticationResponse.builder()
        .accessToken(token)
        .isAuthenticated(true)
        .build();
  }

  public void logout(LogoutRequest request)
      throws ParseException, JOSEException
  {
    try
    {
      var signToken = verifyToken(request.getToken(), true);

      String jit = signToken.getJWTClaimsSet().getJWTID();
      Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

      InvalidatedToken invalidatedToken = InvalidatedToken.builder()
          .id(jit)
          .expiryTime(expiryTime)
          .build();

      invalidatedTokenRepository.save(invalidatedToken);
    } catch (AppException e)
    {
      log.info("Token already expired");
    }
  }

  public AuthenticationResponse refreshToken(RefreshRequest request)
      throws ParseException, JOSEException
  {
    var signedJWT = verifyToken(request.getToken(), true);

    var jit = signedJWT.getJWTClaimsSet().getJWTID();
    var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

    InvalidatedToken invalidatedToken = InvalidatedToken.builder()
        .id(jit)
        .expiryTime(expiryTime)
        .build();

    invalidatedTokenRepository.save(invalidatedToken);

    var username = signedJWT.getJWTClaimsSet().getSubject();
    var user = userRepository.findByUsername(username)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    var token = generateToken(user);

    return AuthenticationResponse.builder()
        .accessToken(token)
        .isAuthenticated(true)
        .build();
  }

  private String generateToken(User user)
  {
    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
        .subject(user.getUsername())
        .issuer("Code Campus")
        .issueTime(new Date())
        .expirationTime(new Date(
            Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
        ))
        .jwtID(UUID.randomUUID().toString())
        .claim("scope", buildScope(user))
        .build();

    Payload payload = new Payload(jwtClaimsSet.toJSONObject());

    JWSObject jwsObject = new JWSObject(header, payload);

    try
    {
      jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

      return jwsObject.serialize();
    } catch (JOSEException e)
    {
      log.error("Cannot create token", e);
      throw new AppException(ErrorCode.FAILED_GENERATE_TOKEN);
    }
  }

  private SignedJWT verifyToken(
      String token,
      boolean isRefresh) throws JOSEException, ParseException
  {
    JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

    SignedJWT signedJWT = SignedJWT.parse(token);

    Date expiryTime = (isRefresh)
        ? new Date(signedJWT.getJWTClaimsSet()
        .getIssueTime()
        .toInstant()
        .plus(REFRESH_DURATION, ChronoUnit.SECONDS)
        .toEpochMilli()
    )
        : signedJWT.getJWTClaimsSet()
        .getExpirationTime();

    var verified = signedJWT.verify(verifier);

    if (!(verified && expiryTime.after(new Date())))
    {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
    {
      throw new AppException(ErrorCode.TOKEN_REVOKED);
    }

    return signedJWT;
  }

  private String buildScope(User user)
  {
    StringJoiner stringJoiner = new StringJoiner(" ");

    if (!CollectionUtils.isEmpty(user.getRoles()))
    {
      user.getRoles().forEach(role -> {
        stringJoiner.add("ROLE_" + role.getName());
        if (!CollectionUtils.isEmpty(role.getPermissions()))
        {
          role.getPermissions().forEach(permission -> {
            stringJoiner.add(permission.getName());
          });
        }
      });
    }

    return stringJoiner.toString();
  }
}
