package com.codecampus.identity.service.account;

import static com.codecampus.identity.constant.authentication.AuthenticationConstant.USER_ROLE;

import com.codecampus.identity.dto.common.PageResponse;
import com.codecampus.identity.dto.request.authentication.PasswordCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserUpdateRequest;
import com.codecampus.identity.dto.response.authentication.UserResponse;
import com.codecampus.identity.entity.account.Role;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.mapper.authentication.UserMapper;
import com.codecampus.identity.mapper.mapper.UserProfileMapper;
import com.codecampus.identity.repository.account.RoleRepository;
import com.codecampus.identity.repository.account.UserRepository;
import com.codecampus.identity.repository.httpclient.profile.ProfileClient;
import com.codecampus.identity.service.authentication.OtpService;
import com.codecampus.identity.utils.AuthenticationUtils;
import com.codecampus.identity.utils.SecurityUtils;
import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
  UserProfileMapper userProfileMapper;

  PasswordEncoder passwordEncoder;
  ProfileClient profileClient;
  AuthenticationUtils authenticationUtils;

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public UserResponse createUser(UserCreationRequest request)
  {
    authenticationUtils.checkExistsUsernameEmail(
        request.getUsername(),
        request.getEmail()
    );

    User user = userMapper.toUser(request);
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    HashSet<Role> roles = new HashSet<>();
    roleRepository.findById(USER_ROLE)
        .ifPresent(roles::add);
    user.setRoles(roles);
    user.setEnabled(true);

    try
    {
      user = userRepository.save(user);
    } catch (DataIntegrityViolationException e)
    {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
    }

    var userProfileRequest =
        userProfileMapper.toUserProfileCreationRequest(request);
    userProfileRequest.setUserId(user.getId());

    var userProfile = profileClient.createUserProfile(userProfileRequest);

    var userCreationResponse = userMapper.toUserResponse(user);
    userCreationResponse.setId(userProfile.getResult().getId());

    return userCreationResponse;
  }

  public void createPassword(PasswordCreationRequest request) {
    User user = findUser(SecurityUtils.getMyUserId());

    if (StringUtils.hasText(request.getPassword())) {
      throw new AppException(ErrorCode.PASSWORD_ALREADY_EXISTS);
    }

    user.setPassword(passwordEncoder.encode(request.getPassword()));
    userRepository.save(user);
  }

  public UserResponse getMyInfo(){
    return getUser(SecurityUtils.getMyUserId());
  }

  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse updateUser(
      String userId,
      UserUpdateRequest request)
  {
    User user = findUser(userId);
    userMapper.updateUser(user, request);
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    var roles = roleRepository.findAllById(request.getRoles());
    user.setRoles(new HashSet<>(roles));

    return userMapper.toUserResponse(userRepository.save(user));
  }

   @PostAuthorize("returnObject.username == authentication.name")
  public UserResponse updateMyInfo(
      UserUpdateRequest request)
  {
    User user = findUser(SecurityUtils.getMyUserId());

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
  public PageResponse<UserResponse> getUsers(int page, int size)
  {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userRepository.findAll(pageable);
    var userList = pageData
        .getContent()
        .stream()
        .map(userMapper::toUserResponse)
        .toList();

    return PageResponse.<UserResponse>builder()
        .currentPage(page)
        .pageSize(pageData.getSize())
        .totalPages(pageData.getTotalPages())
        .totalElements(pageData.getTotalElements())
        .data(userList)
        .build();
  }

  public UserResponse getUser(String id) {
    return userMapper.toUserResponse(
        userRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND))
    );
  }

  public User findUser(String id) {
    return userRepository.findById(id)
            .orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND)
            );
  }
}
