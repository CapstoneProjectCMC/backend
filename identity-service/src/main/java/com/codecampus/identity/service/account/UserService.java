package com.codecampus.identity.service.account;

import static com.codecampus.identity.constant.authentication.AuthenticationConstant.ADMIN_ROLE;
import static com.codecampus.identity.constant.authentication.AuthenticationConstant.STUDENT_ROLE;
import static com.codecampus.identity.constant.authentication.AuthenticationConstant.TEACHER_ROLE;

import com.codecampus.identity.dto.data.BulkImportResult;
import com.codecampus.identity.dto.request.authentication.ChangePasswordRequest;
import com.codecampus.identity.dto.request.authentication.PasswordCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserUpdateRequest;
import com.codecampus.identity.dto.request.org.BulkAddMembersRequest;
import com.codecampus.identity.dto.request.org.BulkUserCreationRequest;
import com.codecampus.identity.dto.request.org.CreateOrganizationMemberRequest;
import com.codecampus.identity.dto.request.org.MemberInfo;
import com.codecampus.identity.entity.account.Role;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.helper.AuthenticationHelper;
import com.codecampus.identity.helper.UserHelper;
import com.codecampus.identity.mapper.authentication.UserMapper;
import com.codecampus.identity.mapper.kafka.UserPayloadMapper;
import com.codecampus.identity.repository.account.RoleRepository;
import com.codecampus.identity.repository.account.UserRepository;
import com.codecampus.identity.repository.httpclient.org.OrganizationClient;
import com.codecampus.identity.service.kafka.UserEventProducer;
import events.user.data.UserProfileCreationPayload;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
  UserRepository userRepository;
  RoleRepository roleRepository;

  UserMapper userMapper;
  UserPayloadMapper userPayloadMapper;

  PasswordEncoder passwordEncoder;
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

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void createAdmin(
      UserCreationRequest userCreationRequest) {
    createCommonUser(userCreationRequest, ADMIN_ROLE);
  }

  @Transactional
  public User createCommonUser(
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
      UserProfileCreationPayload profilePayload =
          userPayloadMapper.toUserProfileCreationPayloadFromUserCreationRequest(
              request);
      userEventProducer.publishRegisteredUserEvent(
          user, profilePayload);

      return user;
    } catch (DataIntegrityViolationException e) {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public User createUserOrg(
      UserCreationRequest request,
      String roleName,
      String orgId,
      String orgMemberRole,
      String blockId,
      String blockRole) {

    User user = createCommonUser(request, roleName);
    var orgRequest = new CreateOrganizationMemberRequest();
    orgRequest.setUserId(user.getId());
    orgRequest.setScopeType("Organization");
    orgRequest.setScopeId(orgId);
    orgRequest.setRole(orgMemberRole == null ? "Student" : orgMemberRole);
    orgRequest.setActive(true);
    organizationClient.addToOrg(orgId, orgRequest);

    if (blockId != null && !blockId.isBlank()) {
      var bRequest = new CreateOrganizationMemberRequest();
      bRequest.setUserId(user.getId());
      bRequest.setScopeType("Grade");
      bRequest.setScopeId(blockId);
      bRequest.setRole(blockRole == null ? orgRequest.getRole() : blockRole);
      bRequest.setActive(true);
      organizationClient.addToBlock(blockId, bRequest);
    }

    return user;
  }

  /**
   * Tạo nhiều user (JSON) và gắn membership vào Organization/Block theo yêu cầu.
   * - Nếu truyền cả orgId & blockId: add vào org trước, sau đó add vào block.
   * - Nếu chỉ truyền orgId: add vào org.
   * - Nếu chỉ truyền blockId: add vào block (service sẽ tự đảm bảo add vào org của block).
   */
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public BulkImportResult bulkCreateUsersAndAssign(
      BulkUserCreationRequest req) {
    int total = 0, created = 0, skipped = 0;
    java.util.List<String> errors = new java.util.ArrayList<>();

    String defaultRoleName =
        req.getDefaultRole() == null || req.getDefaultRole().isBlank()
            ? STUDENT_ROLE
            : req.getDefaultRole().toUpperCase(Locale.ROOT);

    java.util.List<MemberInfo> orgMembers = new java.util.ArrayList<>();
    java.util.List<MemberInfo> blockMembers = new java.util.ArrayList<>();

    for (UserCreationRequest u : req.getUsers()) {
      total++;
      try {
        // tạo user với role hệ thống
        User user = createCommonUser(
            // đảm bảo password có trong request; nếu không, dùng tempPassword
            userHelper.ensurePassword(u),
            defaultRoleName
        );

        // gom membership để gọi bulk 1 lượt
        if (req.getOrgId() != null && !req.getOrgId().isBlank()) {
          orgMembers.add(MemberInfo.builder()
              .userId(user.getId())
              .role(userHelper.normalizeOrgRole(req.getOrgMemberRole()))
              .active(true)
              .build());
        }
        if (req.getBlockId() != null && !req.getBlockId().isBlank()) {
          blockMembers.add(MemberInfo.builder()
              .userId(user.getId())
              .role(userHelper.normalizeOrgRole(
                  req.getBlockRole() != null ? req.getBlockRole()
                      : req.getOrgMemberRole()))
              .active(true)
              .build());
        }

        created++;
      } catch (Exception ex) {
        skipped++;
        errors.add("User " + u.getUsername() + "/" + u.getEmail() + ": " +
            ex.getMessage());
      }
    }

    // gọi org-service bulk (nếu có)
    if (!orgMembers.isEmpty()) {
      organizationClient.bulkAddToOrg(
          req.getOrgId(),
          BulkAddMembersRequest.builder()
              .active(true)
              .defaultRole(null) // đã set trên từng item
              .members(orgMembers)
              .build()
      );
    }
    if (!blockMembers.isEmpty()) {
      organizationClient.bulkAddToBlock(
          req.getBlockId(),
          BulkAddMembersRequest.builder()
              .active(true)
              .defaultRole(null)
              .members(blockMembers)
              .build()
      );
    }

    return new BulkImportResult(total, created, skipped, errors);
  }

  /**
   * Import users từ Excel, sau đó add tất cả vào 1 Organization cố định.
   * File header tương tự /users/import, chỉ cần "username", "email", có thể bỏ cột orgId.
   */
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public BulkImportResult importUsersToOrg(String orgId, String orgMemberRole,
                                           MultipartFile file) {
    int total = 0, created = 0, skipped = 0;
    java.util.List<String> errors = new java.util.ArrayList<>();
    java.util.List<MemberInfo> members = new java.util.ArrayList<>();

    try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = wb.getSheetAt(0);

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
        String roleName = Optional.of(getString(r, 5)).filter(s -> !s.isBlank())
            .orElse(STUDENT_ROLE).toUpperCase(Locale.ROOT);

        if (email.isBlank() || username.isBlank()) {
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
            .orElseGet(() -> roleRepository.save(
                Role.builder().name(roleName).description(roleName).build()));

        User user = userRepository.save(User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(tempPassword))
            .roles(Set.of(role))
            .enabled(true)
            .build());

        // event register + profile
        UserCreationRequest userCreationRequest = UserCreationRequest.builder()
            .username(username).email(email).firstName(firstName)
            .lastName(lastName).displayName(display).build();
        UserProfileCreationPayload profilePayload =
            userPayloadMapper.toUserProfileCreationPayloadFromUserCreationRequest(
                userCreationRequest);
        userEventProducer.publishRegisteredUserEvent(user, profilePayload);

        members.add(MemberInfo.builder()
            .userId(user.getId())
            .role(userHelper.normalizeOrgRole(orgMemberRole))
            .active(true)
            .build());

        created++;
      }

      if (!members.isEmpty()) {
        organizationClient.bulkAddToOrg(orgId,
            BulkAddMembersRequest.builder()
                .active(true)
                .defaultRole(null)
                .members(members)
                .build());
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return new BulkImportResult(total, created, skipped, errors);
  }

  /**
   * Import users từ Excel, sau đó add tất cả vào 1 Block cố định.
   * Organization service sẽ tự đảm bảo join org (nếu user chưa ở org của block).
   */
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public BulkImportResult importUsersToBlock(String blockId, String blockRole,
                                             MultipartFile file) {
    int total = 0, created = 0, skipped = 0;
    java.util.List<String> errors = new java.util.ArrayList<>();
    java.util.List<MemberInfo> members = new java.util.ArrayList<>();

    try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = wb.getSheetAt(0);

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
        String roleName = Optional.of(getString(r, 5)).filter(s -> !s.isBlank())
            .orElse(STUDENT_ROLE).toUpperCase(Locale.ROOT);

        if (email.isBlank() || username.isBlank()) {
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
            .orElseGet(() -> roleRepository.save(
                Role.builder().name(roleName).description(roleName).build()));

        User user = userRepository.save(User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(tempPassword))
            .roles(Set.of(role))
            .enabled(true)
            .build());

        // event register + profile
        UserCreationRequest userCreationRequest = UserCreationRequest.builder()
            .username(username).email(email).firstName(firstName)
            .lastName(lastName).displayName(display).build();
        UserProfileCreationPayload profilePayload =
            userPayloadMapper.toUserProfileCreationPayloadFromUserCreationRequest(
                userCreationRequest);
        userEventProducer.publishRegisteredUserEvent(user, profilePayload);

        members.add(MemberInfo.builder()
            .userId(user.getId())
            .role(userHelper.normalizeOrgRole(blockRole))
            .active(true)
            .build());

        created++;
      }

      if (!members.isEmpty()) {
        organizationClient.bulkAddToBlock(blockId,
            BulkAddMembersRequest.builder()
                .active(true)
                .defaultRole(null)
                .members(members)
                .build());
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return new BulkImportResult(total, created, skipped, errors);
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

  public void changeMyPassword(ChangePasswordRequest req) {
    var user = userHelper.getUserById(AuthenticationHelper.getMyUserId());
    if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
      throw new AppException(
          ErrorCode.INVALID_CREDENTIALS);
    }
    user.setPassword(passwordEncoder.encode(req.getNewPassword()));
    userRepository.save(user);
    userEventProducer.publishUpdatedUserEvent(user);
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
  @Transactional
  public BulkImportResult importUsers(MultipartFile file) {
    int total = 0, created = 0, skipped = 0;
    List<String> errors = new ArrayList<>();

    // Gom membership theo từng scope để gọi bulk theo nhóm
    Map<String, List<MemberInfo>> orgMembersByOrgId = new HashMap<>();
    Map<String, List<MemberInfo>> blockMembersByBlockId = new HashMap<>();

    try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = wb.getSheetAt(0);

      // Chỉ số cột (0-based)
      final int IDX_USERNAME = 0; // A
      final int IDX_EMAIL = 1; // B
      final int IDX_FIRST_NAME = 2; // C
      final int IDX_LAST_NAME = 3; // D
      final int IDX_DISPLAY_NAME = 4; // E
      final int IDX_USER_ROLE = 5; // F -> ADMIN|TEACHER|STUDENT
      final int IDX_ORG_ID = 6; // G
      final int IDX_ORG_MEMBER_ROLE = 7; // H -> Admin|Teacher|Student
      final int IDX_BLOCK_ID = 8; // I
      final int IDX_BLOCK_ROLE = 9; // J -> Admin|Teacher|Student
      final int IDX_ACTIVE = 10; // K -> true|false

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        total++;
        Row r = sheet.getRow(i);
        if (r == null) {
          skipped++;
          errors.add("Row " + (i + 1) + ": empty row");
          continue;
        }

        try {
          // Đọc dữ liệu
          String username = getString(r, IDX_USERNAME);
          String email = getString(r, IDX_EMAIL);
          String firstName = getString(r, IDX_FIRST_NAME);
          String lastName = getString(r, IDX_LAST_NAME);
          String displayName = getString(r, IDX_DISPLAY_NAME);
          String userRoleRaw = getString(r, IDX_USER_ROLE);

          String orgId = getString(r, IDX_ORG_ID);
          String orgRoleRaw = getString(r, IDX_ORG_MEMBER_ROLE);

          String blockId = getString(r, IDX_BLOCK_ID);
          String blockRoleRaw = getString(r, IDX_BLOCK_ROLE);

          Boolean active = parseBoolean(getString(r, IDX_ACTIVE), true);

          // Validate tối thiểu
          if (username.isBlank() || email.isBlank()) {
            skipped++;
            errors.add("Row " + (i + 1) + ": missing username/email");
            continue;
          }

          // Skip nếu user đã tồn tại
          if (userRepository.existsByUsernameOrEmail(username, email)) {
            skipped++;
            continue;
          }

          // Xác định ROLE hệ thống cho user
          String roleName = (userRoleRaw == null || userRoleRaw.isBlank())
              ? STUDENT_ROLE
              : userRoleRaw.trim().toUpperCase(Locale.ROOT);

          Role sysRole = roleRepository
              .findById(roleName)
              .orElseGet(() -> roleRepository.save(
                  Role.builder().name(roleName).description(roleName).build()));

          // Tạo user (password tạm), enable = true
          User user = userRepository.save(User.builder()
              .username(username)
              .email(email)
              .password(passwordEncoder.encode(tempPassword))
              .roles(Set.of(sysRole))
              .enabled(true)
              .build());

          // Gửi event đăng ký kèm profile payload
          UserCreationRequest createReq = UserCreationRequest.builder()
              .username(username)
              .email(email)
              .firstName(firstName)
              .lastName(lastName)
              .displayName(displayName)
              .build();

          UserProfileCreationPayload profilePayload =
              userPayloadMapper.toUserProfileCreationPayloadFromUserCreationRequest(
                  createReq);
          userEventProducer.publishRegisteredUserEvent(user, profilePayload);

          // Gom membership theo nhóm (org / block)
          if (orgId != null && !orgId.isBlank()) {
            String orgMemberRole = userHelper.normalizeOrgRole(orgRoleRaw);
            orgMembersByOrgId
                .computeIfAbsent(orgId, k -> new ArrayList<>())
                .add(MemberInfo.builder()
                    .userId(user.getId())
                    .role(orgMemberRole)
                    .active(active)
                    .build());
          }

          if (blockId != null && !blockId.isBlank()) {
            String blockMemberRole = userHelper.normalizeOrgRole(blockRoleRaw);
            blockMembersByBlockId
                .computeIfAbsent(blockId, k -> new ArrayList<>())
                .add(MemberInfo.builder()
                    .userId(user.getId())
                    .role(blockMemberRole)
                    .active(active)
                    .build());
          }

          created++;
        } catch (Exception exRow) {
          skipped++;
          errors.add("Row " + (i + 1) + ": " + exRow.getMessage());
        }
      }

      // Bulk add vào ORG theo từng orgId
      for (Map.Entry<String, List<MemberInfo>> e : orgMembersByOrgId.entrySet()) {
        try {
          BulkAddMembersRequest req = BulkAddMembersRequest.builder()
              .active(
                  true)             // default cho member nào không set active
              .defaultRole("Student")   // default cho member nào không set role
              .members(e.getValue())
              .build();
          organizationClient.bulkAddToOrg(e.getKey(), req);
        } catch (Exception ex) {
          errors.add("Bulk add members to org " + e.getKey() + " failed: " +
              ex.getMessage());
        }
      }

      // Bulk add vào BLOCK theo từng blockId
      for (Map.Entry<String, List<MemberInfo>> e : blockMembersByBlockId.entrySet()) {
        try {
          BulkAddMembersRequest req = BulkAddMembersRequest.builder()
              .active(true)
              .defaultRole("Student")
              .members(e.getValue())
              .build();
          organizationClient.bulkAddToBlock(e.getKey(), req);
        } catch (Exception ex) {
          errors.add("Bulk add members to block " + e.getKey() + " failed: " +
              ex.getMessage());
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return new BulkImportResult(total, created, skipped, errors);
  }

  /* ===== Helpers (nếu bạn chưa có) ===== */

  private Boolean parseBoolean(String raw, boolean dft) {
    if (raw == null || raw.isBlank()) {
      return dft;
    }
    return "true".equalsIgnoreCase(raw) || "1".equals(raw);
  }

  // helper
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
