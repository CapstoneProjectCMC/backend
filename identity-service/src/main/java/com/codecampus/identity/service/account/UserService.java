package com.codecampus.identity.service.account;

import static com.codecampus.identity.constant.authentication.AuthenticationConstant.STUDENT_ROLE;
import static com.codecampus.identity.constant.authentication.AuthenticationConstant.TEACHER_ROLE;

import com.codecampus.identity.dto.data.BulkImportResult;
import com.codecampus.identity.dto.request.authentication.PasswordCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserUpdateRequest;
import com.codecampus.identity.dto.request.org.CreateOrganizationMemberRequest;
import com.codecampus.identity.entity.account.Role;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.helper.AuthenticationHelper;
import com.codecampus.identity.helper.UserHelper;
import com.codecampus.identity.mapper.authentication.UserMapper;
import com.codecampus.identity.mapper.client.UserProfileMapper;
import com.codecampus.identity.mapper.kafka.UserPayloadMapper;
import com.codecampus.identity.repository.account.RoleRepository;
import com.codecampus.identity.repository.account.UserRepository;
import com.codecampus.identity.repository.httpclient.org.OrganizationClient;
import com.codecampus.identity.repository.httpclient.profile.ProfileClient;
import com.codecampus.identity.service.authentication.OtpService;
import com.codecampus.identity.service.kafka.UserEventProducer;
import events.user.data.UserProfileCreationPayload;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Dịch vụ quản lý người dùng (User) trong hệ thống.
 *
 * <p>Cung cấp các chức năng:
 * <ul>
 *   <li>Tạo mới người dùng và cấu hình mật khẩu.</li>
 *   <li>Cập nhật thông tin người dùng hoặc mật khẩu.</li>
 *   <li>Xóa người dùng theo ID.</li>
 *   <li>Lấy danh sách người dùng có phân trang.</li>
 *   <li>Lấy thông tin chi tiết của người dùng.</li>
 * </ul>
 * Chú ý một số phương thức chỉ dành cho ADMIN hoặc cho phép người dùng tự thao tác.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
  OtpService otpService;
  UserRepository userRepository;
  RoleRepository roleRepository;

  UserMapper userMapper;
  UserProfileMapper userProfileMapper;
  UserPayloadMapper userPayloadMapper;

  PasswordEncoder passwordEncoder;
  ProfileClient profileClient;
  OrganizationClient organizationClient;
  AuthenticationHelper authenticationHelper;
  UserHelper userHelper;
  UserEventProducer userEventProducer;

  @Value("${app.init.tempPassword}")
  @NonFinal
  String tempPassword;

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void createStudent(
      UserCreationRequest userCreationRequest) {
    createCommonUser(userCreationRequest, STUDENT_ROLE);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void createTeacher(
      UserCreationRequest userCreationRequest) {
    createCommonUser(userCreationRequest, TEACHER_ROLE);
  }

  @Transactional
  public void createCommonUser(
      UserCreationRequest request,
      String roleName) {
    authenticationHelper.checkExistsUsernameEmail(
        request.getUsername(),
        request.getEmail()
    );

    User user = userMapper.toUserFromUserCreationRequest(request);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    HashSet<Role> roles = new HashSet<>();
    roleRepository.findById(roleName)
        .ifPresent(roles::add);
    user.setRoles(roles);
    user.setEnabled(true);

    try {
      user = userRepository.save(user);
      if (StringUtils.hasText(request.getOrganizationId())) {
        CreateOrganizationMemberRequest
            createOrganizationMemberRequest =
            new CreateOrganizationMemberRequest();
        createOrganizationMemberRequest.setUserId(user.getId());
        createOrganizationMemberRequest.setScopeId(
            request.getOrganizationId());
        createOrganizationMemberRequest.setRole(
            StringUtils.hasText(request.getOrganizationMemberRole())
                ? request.getOrganizationMemberRole()
                : "Student"
        );
        organizationClient.createMembership(
            createOrganizationMemberRequest);
      }
      UserProfileCreationPayload profilePayload =
          userPayloadMapper.toUserProfileCreationPayloadFromUserCreationRequest(
              request);
      userEventProducer.publishRegisteredUserEvent(
          user, profilePayload);
    } catch (DataIntegrityViolationException e) {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
    }
  }

  /**
   * Tạo hoặc cập nhật mật khẩu cho người dùng hiện tại.
   * <p>
   * - Ném lỗi nếu đã tồn tại mật khẩu.
   *
   * @param request chứa mật khẩu mới
   * @throws AppException nếu mật khẩu đã tồn tại
   */
  public void createPassword(PasswordCreationRequest request) {
    User user = userHelper.getUserById(AuthenticationHelper.getMyUserId());

    if (StringUtils.hasText(request.getPassword())) {
      throw new AppException(ErrorCode.PASSWORD_ALREADY_EXISTS);
    }

    user.setPassword(passwordEncoder.encode(request.getPassword()));
    userRepository.save(user);
  }

  /**
   * Cập nhật thông tin người dùng theo ID.
   *
   * <p>Chỉ ADMIN được phép gọi.
   * Mã hóa lại mật khẩu và cập nhật vai trò.
   * </p>
   *
   * @param userId  ID người dùng cần cập nhật
   * @param request thông tin cập nhật
   */
  @PreAuthorize("hasRole('ADMIN')")
  public void updateUserById(
      String userId,
      UserUpdateRequest request) {
    User user = userHelper.getUserById(userId);
    updateUser(request, user);
  }

  /**
   * Cập nhật thông tin của chính người dùng đang đăng nhập.
   *
   * <p>Chỉ cho phép khi username trả về khớp tên trong authentication.</p>
   *
   * @param request thông tin cập nhật
   */
  public void updateMyInfo(
      UserUpdateRequest request) {
    User user = userHelper.getUserById(AuthenticationHelper.getMyUserId());
    updateUser(request, user);
  }

  private void updateUser(
      UserUpdateRequest request,
      User user) {
    userMapper.updateUserUpdateRequestToUser(user, request);
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    if (request.getRoles() != null) {
      List<Role> roles = roleRepository.findAllById(request.getRoles());
      user.setRoles(new HashSet<>(roles));
    }
    userRepository.save(user);

    userEventProducer.publishUpdatedUserEvent(user);
  }

  /**
   * Xóa người dùng theo ID.
   *
   * <p>Chỉ ADMIN được phép gọi.</p>
   *
   * @param userId ID người dùng cần xóa
   */
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void deleteUser(String userId) {
    User user = userHelper.getUserById(userId);
    user.markDeleted(AuthenticationHelper.getMyEmail());
    userRepository.save(user);

    userEventProducer.publishDeletedUserEvent(user);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void restoreUser(String userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    if (user.getDeletedAt() != null) {
      user.setDeletedAt(null);
      user.setDeletedBy(null);
      userRepository.save(user);
      userEventProducer.publishRestoredUserEvent(user);
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  public BulkImportResult importUsers(
      MultipartFile file) {
    int total = 0, created = 0, skipped = 0;
    List<String> errors = new ArrayList<>();

    try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = wb.getSheetAt(0);
      // Header: username,email,firstName,lastName,displayName,
      // role(ADMIN|TEACHER|STUDENT),
      // organizationId,organizationMemberRole
      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        total++;
        Row r = sheet.getRow(i);
        if (r == null) {
          skipped++;
          continue;
        }

        String username = getString(r, 0);
        String email = getString(r, 1);
        String firstName = getString(r, 2);
        String lastName = getString(r, 3);
        String display = getString(r, 4);
        String roleName =
            Optional.of(getString(r, 5))
                .filter(s -> !s.isBlank())
                .orElse(STUDENT_ROLE)
                .toUpperCase(Locale.ROOT);
        String orgId = getString(r, 6);
        String orgMember = getString(r, 7);

        if (email == null || username == null) {
          skipped++;
          errors.add("Row " + (i + 1) + ": missing username/email");
          continue;
        }
        if (userRepository.findByUsername(username).isPresent() ||
            userRepository.findByEmail(email).isPresent()) {
          skipped++;
          continue;
        }

        Role role = roleRepository
            .findById(roleName)
            .orElseGet(
                () -> roleRepository.save(
                    Role.builder().name(roleName).description(roleName)
                        .build()));

        User user = userRepository.save(User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(tempPassword))
            .roles(Set.of(role))
            .enabled(true)
            .build());

        // gửi event kèm dữ liệu profile
        UserCreationRequest userCreationRequest =
            UserCreationRequest.builder()
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .displayName(display)
                .organizationId(orgId)
                .organizationMemberRole(orgMember)
                .build();
        UserProfileCreationPayload profilePayload =
            userPayloadMapper.toUserProfileCreationPayloadFromUserCreationRequest(
                userCreationRequest);
        userEventProducer.publishRegisteredUserEvent(user, profilePayload);

        created++;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return new BulkImportResult(total, created, skipped, errors);
  }

  @PreAuthorize("hasRole('ADMIN')")
  public void exportUsers(HttpServletResponse response) {
    // Tạo file excel, không xuất password
    try (Workbook wb = new XSSFWorkbook()) {
      Sheet sheet = wb.createSheet("users");
      Row row = sheet.createRow(0);
      String[] headers = {
          "username",
          "email",
          "firstName",
          "lastName",
          "displayName",
          "roles",
          "active",
          "createdAt"
      };
      for (int c = 0; c < headers.length; c++) {
        row.createCell(c).setCellValue(headers[c]);
      }

      int rowIdx = 1;
      for (User u : userRepository.findAll()) {
        Row r = sheet.createRow(rowIdx++);
        r.createCell(0).setCellValue(Objects.toString(u.getUsername(), ""));
        r.createCell(1).setCellValue(Objects.toString(u.getEmail(), ""));
        r.createCell(2).setCellValue("");
        r.createCell(3).setCellValue("");
        r.createCell(4).setCellValue("");
        r.createCell(5).setCellValue(String.join(",",
            u.getRoles().stream().map(Role::getName).toList()));
        r.createCell(6).setCellValue(u.isEnabled());
        r.createCell(7).setCellValue(Objects.toString(
            Optional.ofNullable(u.getCreatedAt()).orElse(Instant.now()), ""));
      }

      response.setContentType(
          "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setHeader("Content-Disposition",
          "attachment; filename=users.xlsx");
      try (OutputStream os = response.getOutputStream()) {
        wb.write(os);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String getString(Row r, int idx) {
    if (r.getCell(idx) == null) {
      return "";
    }
    r.getCell(idx).setCellType(CellType.STRING);
    String s = r.getCell(idx).getStringCellValue();
    return s != null ? s.trim() : "";
  }
}
