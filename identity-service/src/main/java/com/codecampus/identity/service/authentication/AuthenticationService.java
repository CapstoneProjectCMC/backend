package com.codecampus.identity.service.authentication;

import com.codecampus.identity.dto.request.authentication.LoginRequest;
import com.codecampus.identity.dto.request.authentication.RefreshTokenRequest;
import com.codecampus.identity.dto.request.authentication.RegisterRequest;
import com.codecampus.identity.dto.response.authentication.LoginResponse;
import com.codecampus.identity.entity.account.Token;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.repository.account.TokenRepository;
import com.codecampus.identity.repository.account.UserRepository;
import java.time.Instant;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService
{
  AuthenticationManager authenticationManager;

  TokenService tokenService;
  JwtTokenService jwtTokenService;
  RoleService roleService;

  PasswordEncoder passwordEncoder;

  UserRepository userRepository;
  private final TokenRepository tokenRepository;

  // Phương thức đăng ký
  public void register(
      RegisterRequest registerRequest)
  {
    // Kiểm tra user đã tồn tại hay chưa
    if (userRepository.existsByUsernameOrEmail(
        registerRequest.getUsername(),
        registerRequest.getEmail()))
    {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
    }

    // Tạo User mới
    User user = User.builder()
        .username(registerRequest.getUsername())
        .email(registerRequest.getEmail())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .roles(Collections.singleton(roleService.getDefaultRole()))
        .build();

    userRepository.save(user);
  }

  // Phương thức đăng nhập
  public LoginResponse login(
      LoginRequest request)
  {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getUsernameOrEmail(),
            request.getPassword()
        )
    );

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    User user = userRepository
        .findByUsername(userDetails.getUsername())
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );

    // Sinh token
    String accessToken = jwtTokenService.generateAccessToken(userDetails);
    String refreshToken = jwtTokenService.generateRefreshToken(userDetails);
    Instant expiryTime = jwtTokenService.getExpirationFromToken(accessToken);

    // Lưu token vào trong database
    Token token = tokenService.createToken(
        user,
        accessToken,
        refreshToken,
        expiryTime
    );

    String roles = user.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    return LoginResponse.builder()
        .username(user.getUsername())
        .email(user.getEmail())
        .role(roles)
        .tokenId(token.getId())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .expiryTime(expiryTime.toString())
        .isAuthenticated(true)
        .build();
  }

  // Phương thức đăng xuất
  public void logout()
  {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null
        && authentication.getCredentials() instanceof String token)
    {
      tokenService.revokeToken(token);
    }

    // Xoá thông tin xác thực khỏi Security Context Holder
    SecurityContextHolder.clearContext();
  }

  // Refresh Token
  public LoginResponse refreshToken(
      RefreshTokenRequest request)
  {
    Token token = tokenService.refreshAccessToken(request);
    User user = token.getUser();

    // Sinh ra access token mới
    String newAccessToken = jwtTokenService.generateAccessToken(user);
    Instant newExpiryTime = jwtTokenService.getExpirationFromToken(newAccessToken);

    // Update token trong database
    token.setAccessToken(newAccessToken);
    token.setExpiryTime(newExpiryTime);
    tokenRepository.save(token);

    return LoginResponse.builder()
        .username(user.getUsername())
        .email(user.getEmail())
        .role(user.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","))
        )
        .tokenId(token.getId())
        .tokenAccessType("Bearer")
        .accessToken(newAccessToken)
        .refreshToken(token.getRefreshToken())
        .expiryTime(newExpiryTime.toString())
        .isAuthenticated(true)
        .build();
  }
}
