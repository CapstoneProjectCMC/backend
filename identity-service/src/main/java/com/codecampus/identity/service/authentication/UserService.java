package com.codecampus.identity.service.authentication;

import static com.codecampus.identity.constant.authentication.AuthenticationConstant.USER_ROLE;

import com.codecampus.identity.dto.request.authentication.PasswordCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserUpdateRequest;
import com.codecampus.identity.dto.response.authentication.UserResponse;
import com.codecampus.identity.entity.account.Role;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.mapper.authentication.UserMapper;
import com.codecampus.identity.repository.account.RoleRepository;
import com.codecampus.identity.repository.account.UserRepository;
import io.netty.util.internal.StringUtil;
import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService
{
  OtpService otpService;
  UserRepository userRepository;
  RoleRepository roleRepository;
  UserMapper userMapper;
  PasswordEncoder passwordEncoder;

  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse createUser(UserCreationRequest request) {
    // Kiểm tra username và email đã tồn tại
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
    }

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    User user = userMapper.toUser(request);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setEnabled(true);

    HashSet<Role> roles = new HashSet<>();
    roleRepository.findById(USER_ROLE)
        .ifPresent(roles::add);
    user.setRoles(roles);

    try {
      user = userRepository.save(user);
    } catch (DataIntegrityViolationException e) {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
    }

    return userMapper.toUserResponse(user);
  }

  public void createPassword(PasswordCreationRequest request) {
    var context = SecurityContextHolder.getContext();
    String name = context.getAuthentication().getName();

    User user = userRepository.findByUsername(name)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    if (StringUtils.hasText(request.getPassword())) {
      throw new AppException(ErrorCode.PASSWORD_ALREADY_EXISTS);
    }

    user.setPassword(passwordEncoder.encode(request.getPassword()));
    userRepository.save(user);
  }

  public UserResponse getMyInfo(){
    var context = SecurityContextHolder.getContext();
    String username = context.getAuthentication().getName();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    return userMapper.toUserResponse(user);
  }

  // @PostAuthorize("returnObject.username == authentication.name")
  public UserResponse updateUser(
      String userId,
      UserUpdateRequest request)
  {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    userMapper.updateUser(user, request);
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    var roles = roleRepository.findAllById(request.getRoles());
    user.setRoles(new HashSet<>(roles));

    return userMapper.toUserResponse(userRepository.save(user));
  }

  @PreAuthorize("hasRole('ADMIN')")
  public void deleteUser(String userId) {
    userRepository.deleteById(userId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  public List<UserResponse> getUsers() {
    return userRepository.findAll()
        .stream()
        .map(userMapper::toUserResponse)
        .toList();
  }

  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse getUser(String id) {
    return userMapper.toUserResponse(
        userRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND))
    );
  }
}
