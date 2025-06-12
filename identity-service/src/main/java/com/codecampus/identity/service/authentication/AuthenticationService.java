package com.codecampus.identity.service.authentication;

import static com.codecampus.identity.constant.authentication.AuthenticationConstant.USER_ROLE;

import com.codecampus.identity.dto.request.authentication.AuthenticationRequest;
import com.codecampus.identity.dto.request.authentication.ExchangeTokenRequest;
import com.codecampus.identity.dto.request.authentication.IntrospectRequest;
import com.codecampus.identity.dto.request.authentication.LogoutRequest;
import com.codecampus.identity.dto.request.authentication.RefreshRequest;
import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.response.authentication.AuthenticationResponse;
import com.codecampus.identity.dto.response.authentication.IntrospectResponse;
import com.codecampus.identity.entity.account.InvalidatedToken;
import com.codecampus.identity.entity.account.Role;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.mapper.authentication.UserMapper;
import com.codecampus.identity.mapper.mapper.UserProfileMapper;
import com.codecampus.identity.repository.account.InvalidatedTokenRepository;
import com.codecampus.identity.repository.account.RoleRepository;
import com.codecampus.identity.repository.account.UserRepository;
import com.codecampus.identity.repository.httpclient.google.OutboundGoogleIdentityClient;
import com.codecampus.identity.repository.httpclient.google.OutboundGoogleUserClient;
import com.codecampus.identity.repository.httpclient.profile.ProfileClient;
import com.codecampus.identity.utils.AuthenticationUtils;
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
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService
{
  UserRepository userRepository;
  RoleRepository roleRepository;
  InvalidatedTokenRepository invalidatedTokenRepository;
  OtpService otpService;

  UserProfileMapper userProfileMapper;
  UserMapper userMapper;

  PasswordEncoder passwordEncoder;

  OutboundGoogleIdentityClient outboundGoogleIdentityClient;
  OutboundGoogleUserClient outboundGoogleUserClient;
  ProfileClient profileClient;
  AuthenticationUtils authenticationUtils;

  @NonFinal
  @Value("${app.jwt.signerKey}")
  protected String SIGNER_KEY;

  @NonFinal
  @Value("${app.jwt.valid-duration}")
  protected long VALID_DURATION;

  @NonFinal
  @Value("${app.jwt.refreshable-duration}")
  protected long REFRESH_DURATION;

  @NonFinal
  @Value("${app.google.client-id}")
  protected String GOOGLE_CLIENT_ID;

  @NonFinal
  @Value("${app.google.client-secret}")
  protected String GOOGLE_CLIENT_SECRET;

  @NonFinal
  @Value("${app.google.redirect-uri}")
  protected String GOOGLE_REDIRECT_URI;

  @NonFinal
  protected String GRANT_TYPE = "authorization_code";

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

  public AuthenticationResponse outboundGoogleLogin(
      String code) throws ParseException
  {
    var response = outboundGoogleIdentityClient.exchangeToken(
        ExchangeTokenRequest.builder()
            .code(code)
            .clientId(GOOGLE_CLIENT_ID)
            .clientSecret(GOOGLE_CLIENT_SECRET)
            .redirectUri(GOOGLE_REDIRECT_URI)
            .grantType(GRANT_TYPE)
            .build()
    );
    log.info("TOKEN RESPONSE {}", response);

    // Get User info
    var userInfo = outboundGoogleUserClient.getUserInfo(
        "json",
        response.getAccessToken());

    log.info("User Info {}", userInfo);

    Set<Role> roles = new HashSet<>();
    roles.add(Role.builder()
        .name(USER_ROLE)
        .build()
    );

    // Onboard user
    var user = userRepository.findByUsername(userInfo.getEmail())
        .orElseGet(() -> userRepository.save(User.builder()
            .username(userInfo.getEmail())
            .roles(roles)
            .build()));

    // Tạo token
    var token = generateToken(user);
    SignedJWT signedJWT;
    try {
      signedJWT = SignedJWT.parse(token);
    } catch (ParseException e) {
      throw new AppException(ErrorCode.FAILED_GENERATE_TOKEN);
    }
    JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

    var role = user.getRoles().stream()
        .findFirst()
        .map(Role::getName)
        .orElse(null);

    return AuthenticationResponse.builder()
        .username(user.getUsername())
        .email(user.getEmail())
        .role(role)
        .tokenId(claims.getJWTID())
        .tokenAccessType("Bearer")
        .accessToken(token)
        .refreshToken(null)
        .expiryTime(claims.getExpirationTime().toInstant())
        .isAuthenticated(true)
        .isEnabled(true)
        .build();
  }

  public AuthenticationResponse login(
      AuthenticationRequest request) throws ParseException
  {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    // Tìm user bằng username hoặc email
    var user = userRepository
        .findByUsernameOrEmail(request.getUsername(), request.getEmail())
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    // Kiểm tra tài khoản đã kích hoạt chưa
    // TODO Mới đang ném ra lỗi khi chưa kích hoạt tài khoản
    if (!user.isEnabled()) {
      throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVATED);
    }

    boolean authenticated = passwordEncoder.matches(
        request.getPassword(),
        user.getPassword()
    );

    if (!authenticated)
    {
      throw new AppException(ErrorCode.INVALID_CREDENTIALS);
    }

    // Tạo token
    var token = generateToken(user);
    SignedJWT signedJWT;
    try {
      signedJWT = SignedJWT.parse(token);
    } catch (ParseException e) {
      throw new AppException(ErrorCode.FAILED_GENERATE_TOKEN);
    }
    JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

    var role = user.getRoles().stream()
        .findFirst()
        .map(Role::getName)
        .orElse(null);

    return AuthenticationResponse.builder()
        .username(user.getUsername())
        .email(user.getEmail())
        .role(role)
        .tokenId(claims.getJWTID())
        .tokenAccessType("Bearer")
        .accessToken(token)
        .refreshToken(null)
        .expiryTime(claims.getExpirationTime().toInstant())
        .isAuthenticated(true)
        .isEnabled(true)
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
          .expiryTime(expiryTime.toInstant())
          .build();

      invalidatedTokenRepository.save(invalidatedToken);
    } catch (AppException e)
    {
      log.info("Token already expired");
    }
  }

  @Transactional
  public void register(UserCreationRequest request) {
    authenticationUtils.checkExistsUsernameEmail(
        request.getUsername(),
        request.getEmail()
    );

    // Tạo user nhưng chưa kích hoạt
    User user = userMapper.toUser(request);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setEnabled(false); // Chưa kích hoạt

    // Gán role mặc định
    HashSet<Role> roles = new HashSet<>();
    roleRepository.findById(USER_ROLE).ifPresent(roles::add);
    user.setRoles(roles);

    try {
      userRepository.save(user);

      // Gửi OTP qua email
      otpService.sendOtp(request);

    } catch (DataIntegrityViolationException e) {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
    }

    var userProfileRequest =
        userProfileMapper.toUserProfileCreationRequest(request);
    userProfileRequest.setUserId(user.getId());

    profileClient.createUserProfile(userProfileRequest);
  }

  public AuthenticationResponse refreshToken(RefreshRequest request)
      throws ParseException, JOSEException
  {
    var signedJWT = verifyToken(request.getToken(), true);

    var jit = signedJWT.getJWTClaimsSet().getJWTID();
    var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

    InvalidatedToken invalidatedToken = InvalidatedToken.builder()
        .id(jit)
        .expiryTime(expiryTime.toInstant())
        .build();

    invalidatedTokenRepository.save(invalidatedToken);

    var username = signedJWT.getJWTClaimsSet().getSubject();
    var user = userRepository.findByUsername(username)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    var token = generateToken(user);

    return AuthenticationResponse.builder()
        .accessToken(token)
        .expiryTime(expiryTime.toInstant())
        .isAuthenticated(true)
        .build();
  }

  private String generateToken(User user)
  {
    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

    Instant expiryInstant = Instant.now()
        .plus(VALID_DURATION, ChronoUnit.SECONDS);

    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
        .subject(user.getId())
        .issuer("Code Campus")
        .issueTime(new Date())
        .expirationTime(Date.from(expiryInstant))
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
          role.getPermissions().forEach(permission ->
              stringJoiner.add(permission.getName()));
        }
      });
    }

    return stringJoiner.toString();
  }
}
