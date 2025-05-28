package com.codecampus.identity.service.authentication;

import com.codecampus.identity.dto.request.authentication.LoginRequest;
import com.codecampus.identity.dto.request.authentication.RegisterRequest;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.repository.account.UserRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    AuthenticationManager authenticationManager;

    JwtTokenService jwtTokenService;
    RoleService roleService;

    PasswordEncoder passwordEncoder;

    UserRepository userRepository;

    // Phương thức đăng ký
    public void register(
            RegisterRequest registerRequest) {
        // Kiểm tra user đã tồn tại hay chưa
        if (userRepository.existsByUsernameOrEmail(
                registerRequest.getUsername(),
                registerRequest.getEmail())) {
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
    public String login(
            LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return jwtTokenService.generateToken(userDetails);
    }

    // Phương thức đăng xuất
    public void logout() {
        // Xoá thông tin xác thực khỏi Security Context Holder
        SecurityContextHolder.clearContext();
    }
}
