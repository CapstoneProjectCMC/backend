package com.codecampus.identity.service.authentication;

import static com.codecampus.identity.constant.authentication.AuthenticationConstant.USER_ROLE;

import com.codecampus.identity.dto.request.authentication.AuthenticationRequest;
import com.codecampus.identity.dto.request.authentication.ExchangeTokenRequest;
import com.codecampus.identity.dto.request.authentication.IntrospectRequest;
import com.codecampus.identity.dto.request.authentication.RefreshRequest;
import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.response.authentication.AuthenticationResponse;
import com.codecampus.identity.dto.response.authentication.ExchangeTokenResponse;
import com.codecampus.identity.dto.response.authentication.IntrospectResponse;
import com.codecampus.identity.dto.response.authentication.OutboundUserResponse;
import com.codecampus.identity.entity.account.InvalidatedToken;
import com.codecampus.identity.entity.account.Permission;
import com.codecampus.identity.entity.account.Role;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.helper.AuthenticationHelper;
import com.codecampus.identity.helper.ProfileSyncHelper;
import com.codecampus.identity.mapper.authentication.UserMapper;
import com.codecampus.identity.repository.account.InvalidatedTokenRepository;
import com.codecampus.identity.repository.account.RoleRepository;
import com.codecampus.identity.repository.account.UserRepository;
import com.codecampus.identity.repository.httpclient.google.OutboundGoogleIdentityClient;
import com.codecampus.identity.repository.httpclient.google.OutboundGoogleUserClient;
import com.codecampus.identity.utils.EmailUtils;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
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

/**
 * Dịch vụ xác thực và quản lý phiên làm việc (authentication) cho người dùng.
 *
 * <p>Cung cấp các phương thức:
 * <ul>
 *   <li>Introspect: Kiểm tra tính hợp lệ của token.</li>
 *   <li>login: Xác thực username/password và sinh token.</li>
 *   <li>logout: Thu hồi token bằng cách lưu vào blacklist.</li>
 *   <li>refreshToken: Tạo token mới dựa trên token cũ.</li>
 *   <li>outboundGoogleLogin: Đăng nhập qua Google OAuth2.</li>
 *   <li>register: Đăng ký người dùng mới và gửi OTP.</li>
 * </ul>
 * Đồng thời xử lý việc sinh và xác thực JWT qua HMAC-SHA512.</p>
 */
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

  UserMapper userMapper;

  PasswordEncoder passwordEncoder;

  OutboundGoogleIdentityClient outboundGoogleIdentityClient;
  OutboundGoogleUserClient outboundGoogleUserClient;
  AuthenticationHelper authenticationHelper;
  ProfileSyncHelper profileSyncHelper;

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

  /**
   * Kiểm tra tính hợp lệ của token mà không ném exception.
   *
   * @param request đối tượng chứa token cần kiểm tra
   * @return IntrospectResponse với flag valid
   * @throws JOSEException  khi lỗi ký JWT
   * @throws ParseException khi lỗi parse JWT
   */
  public IntrospectResponse introspect(
      IntrospectRequest request)
      throws JOSEException, ParseException
  {
    String token = request.getToken();
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

  /**
   * Đăng nhập qua Google OAuth2, trao đổi code lấy access token và user info.
   *
   * @param code authorization code từ Google
   * @return AuthenticationResponse chứa accessToken, thông tin user
   * @throws ParseException khi lỗi parse JWT
   */
  public AuthenticationResponse outboundGoogleLogin(
      String code) throws ParseException
  {
    ExchangeTokenResponse response =
        outboundGoogleIdentityClient.exchangeToken(
            ExchangeTokenRequest.builder()
                .code(code)
                .clientId(GOOGLE_CLIENT_ID)
                .clientSecret(GOOGLE_CLIENT_SECRET)
                .redirectUri(GOOGLE_REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build()
        );

    // Get User info
    OutboundUserResponse googleUser = outboundGoogleUserClient.getUserInfo(
        "json", response.getAccessToken());

    // Onboard user
    User user = userRepository
        .findByEmail(googleUser.getEmail())
        .orElseGet(() -> {
              Role userRole = roleRepository.findByName(USER_ROLE);
              User newUser = userRepository.save(User.builder()
                  .username(EmailUtils.extractUsername(googleUser.getEmail()))
                  .email(googleUser.getEmail())
                  .roles(Set.of(userRole))
                  .enabled(true)
                  .build());

              UserCreationRequest googleRequest = UserCreationRequest.builder()
                  .email(googleUser.getEmail())
                  .firstName(googleUser.getGivenName())
                  .lastName(googleUser.getFamilyName())
                  .displayName(googleUser.getName())
                  .build();

              profileSyncHelper.createProfile(newUser, googleRequest);
              return newUser;
            }
        );

    // Tạo token
    return generateTokenAndReturnAuthenticationResponse(user);
  }

  /**
   * Đăng nhập bằng username hoặc email, kiểm tra mật khẩu và sinh JWT.
   *
   * @param request thông tin đăng nhập
   * @return AuthenticationResponse chứa accessToken và thông tin user
   * @throws ParseException khi lỗi parse JWT
   */
  public AuthenticationResponse login(
      AuthenticationRequest request) throws ParseException
  {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    // Tìm user bằng username hoặc email
    User user = userRepository
        .findByUsernameOrEmail(request.getUsername(), request.getEmail())
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    // Kiểm tra tài khoản đã kích hoạt chưa
    // TODO Mới đang ném ra lỗi khi chưa kích hoạt tài khoản
    if (!user.isEnabled())
    {
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
    return generateTokenAndReturnAuthenticationResponse(user);
  }

  /**
   * Đăng xuất, invalid token bằng cách lưu vào repository.
   *
   * @param token chứa token cần logout
   * @throws ParseException khi lỗi parse JWT
   * @throws JOSEException  khi lỗi verify JWT
   */
  public void logout(String token)
      throws ParseException, JOSEException
  {
    try
    {
      SignedJWT signToken = verifyToken(token, true);

      invalidateToken(signToken);
    } catch (AppException e)
    {
      log.info("Token already expired");
    }
  }

  /**
   * Đăng ký người dùng mới chưa kích hoạt và gửi mã OTP.
   *
   * @param request thông tin user để đăng ký
   */
  @Transactional
  public void register(UserCreationRequest request)
  {
    authenticationHelper.checkExistsUsernameEmail(
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

    try
    {
      userRepository.save(user);
      profileSyncHelper.createProfile(user, request);
    } catch (DataIntegrityViolationException e)
    {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
    }

    try
    {
      // Gửi OTP qua email
      otpService.sendOtp(request);
    } catch (AppException e)
    {
      throw new AppException(ErrorCode.FAILED_SEND_EMAIL);
    }
  }

  /**
   * Làm mới access token từ refresh token.
   *
   * @param request chứa token cũ
   * @return AuthenticationResponse chứa token mới và expiryTime
   * @throws ParseException khi lỗi parse JWT
   * @throws JOSEException  khi lỗi verify JWT
   */
  public AuthenticationResponse refreshToken(RefreshRequest request)
      throws ParseException, JOSEException
  {
    SignedJWT signedJWT = verifyToken(request.getToken(), true);

    invalidateToken(signedJWT);

    User user = userRepository
        .findById(signedJWT.getJWTClaimsSet().getSubject())
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    return generateTokenAndReturnAuthenticationResponse(user);
  }

  private void invalidateToken(SignedJWT signedJWT)
      throws ParseException
  {
    String jit = signedJWT.getJWTClaimsSet().getJWTID();
    Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

    InvalidatedToken invalidatedToken = InvalidatedToken.builder()
        .id(jit)
        .expiryTime(expiryTime.toInstant())
        .build();

    invalidatedTokenRepository.save(invalidatedToken);
  }

  /**
   * Sinh JWT mới cho user dựa trên HMAC-SHA512.
   *
   * @param user     đối tượng User cần sinh token
   * @param duration thời lượng (giây) còn hiệu lực
   * @param type     access_token | refresh_token
   * @return chuỗi JWT
   */
  private SignedJWT generateToken(
      User user,
      long duration,
      String type)
  {
    JWSHeader header = new JWSHeader
        .Builder(JWSAlgorithm.HS512)
        .type(JOSEObjectType.JWT)
        .build();

    JWTClaimsSet jwtClaimsSet = buildJwtClaimsSet(user, duration, type);

    SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);

    try
    {
      JWSSigner signer = new MACSigner(SIGNER_KEY.getBytes(StandardCharsets.UTF_8));
      signedJWT.sign(signer);

      return signedJWT;
    } catch (JOSEException e)
    {
      log.error("Cannot create token", e);
      throw new AppException(ErrorCode.FAILED_GENERATE_TOKEN);
    }
  }

  private JWTClaimsSet buildJwtClaimsSet(
      User user,
      long duration,
      String type)
  {
    Instant expiryInstant = Instant.now()
        .plus(duration, ChronoUnit.SECONDS);

    return new JWTClaimsSet.Builder()
        .subject(user.getEmail())
        .issuer("Code Campus")
        .issueTime(new Date())
        .expirationTime(Date.from(expiryInstant))
        .jwtID(UUID.randomUUID().toString())
        .claim("scope", buildScope(user))
        .claim("userId", user.getId())
        .claim("username", user.getUsername())
        .claim("email", user.getEmail())
        .claim("roles", user.getRoles()
            .stream()
            .map(Role::getName)
            .toList()
        )
        .claim("permissions", user.getRoles()
            .stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getName)
            .distinct()
            .toList()
        )
        .claim("active", true)
        .claim("token_type", type)
        .build();
  }

  /**
   * Xác thực và verify JWT.
   *
   * @param token     chuỗi JWT cần verify
   * @param isRefresh nếu true, xét thời gian refresh duration thay vì expiration
   * @return SignedJWT đã verify
   * @throws JOSEException  khi verify HMAC thất bại
   * @throws ParseException khi parse JWT thất bại
   */
  private SignedJWT verifyToken(
      String token,
      boolean isRefresh)
      throws JOSEException, ParseException
  {
    SignedJWT signedJWT = SignedJWT.parse(token);

    Date expiryTime = isRefresh
        ? Date.from(signedJWT.getJWTClaimsSet()
        .getIssueTime()       // thời điểm phát hành
        .toInstant()
        .plus(REFRESH_DURATION, ChronoUnit.SECONDS))
        : signedJWT.getJWTClaimsSet().getExpirationTime();

    boolean verified = signedJWT.verify(
        new MACVerifier(SIGNER_KEY.getBytes(StandardCharsets.UTF_8)));

    if (!(verified && expiryTime.after(new Date())))
    {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    if (invalidatedTokenRepository.existsById(
        signedJWT.getJWTClaimsSet().getJWTID()))
    {
      throw new AppException(ErrorCode.TOKEN_REVOKED);
    }

    return signedJWT;
  }

  /**
   * Xây dựng scope (roles và permissions) cho JWT.
   *
   * @param user đối tượng User
   * @return chuỗi scope phân tách bởi dấu cách
   */
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

  private AuthenticationResponse generateTokenAndReturnAuthenticationResponse(User user)
      throws ParseException
  {
    SignedJWT accessToken = generateToken(user, VALID_DURATION, "access_token");
    SignedJWT refreshToken = generateToken(user, REFRESH_DURATION, "refresh_token");

    return AuthenticationResponse.builder()
        .accessToken(accessToken.serialize())
        .refreshToken(refreshToken.serialize())
        .accessExpiry(accessToken.getJWTClaimsSet().getExpirationTime().toInstant())
        .refreshExpiry(refreshToken.getJWTClaimsSet().getExpirationTime().toInstant())
        .authenticated(true)
        .enabled(user.isEnabled())
        .build();
  }
}
