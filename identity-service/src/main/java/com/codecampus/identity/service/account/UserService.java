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
import com.codecampus.identity.service.kafka.NotificationEventProducer;
import com.codecampus.identity.service.kafka.UserEventProducer;
import events.notification.NotificationEvent;
import events.user.data.UserProfileCreationPayload;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
  NotificationEventProducer notificationEventProducer;

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
   * Tạo hoặc cập nhật mật khẩu cho người dùng hiện tại.
   * <p>
   * - Ném lỗi nếu đã tồn tại mật khẩu.
   *
   * @param request chứa mật khẩu mới
   * @throws AppException nếu mật khẩu đã tồn tại
   */
  public void createPassword(PasswordCreationRequest request) {
    User user = userHelper.getUserById(AuthenticationHelper.getMyUserId());

    if (!StringUtils.hasText(request.getPassword())) {
      throw new AppException(ErrorCode.PASSWORD_ALREADY_EXISTS);
    }

    user.setPassword(passwordEncoder.encode(request.getPassword()));
    userRepository.save(user);

    notificationEventProducer.publish(NotificationEvent.builder()
        .channel("SOCKET")
        .recipient(user.getId())
        .templateCode("PASSWORD_CREATED")
        .param(Map.of())
        .subject("Tạo mật khẩu thành công")
        .body(
            "Bạn đã tạo mật khẩu cho tài khoản. Từ giờ có thể đăng nhập bằng email/mật khẩu.")
        .build()
    );
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
    User user = userRepository.findByIdIncludingDeleted(userId)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    if (user.getDeletedAt() != null) {
      user.setDeletedAt(null);
      user.setDeletedBy(null);
      userRepository.save(user);
      userEventProducer.publishRestoredUserEvent(user);
    }

    // Gửi thông báo realtime + email
    Map<String, Object> param = new HashMap<>();
    param.put("email", user.getEmail());
    param.put("displayName",
        user.getUsername() != null ? user.getUsername() : user.getEmail());

    notificationEventProducer.publish(NotificationEvent.builder()
        .channel("SOCKET, EMAIL")
        .recipient(user.getId())
        .templateCode("ACCOUNT_RESTORED")
        .param(Map.of())
        .subject("Tài khoản đã được khôi phục")
        .body(
            "Xin chào {{displayName}}, tài khoản của bạn đã được khôi phục và có thể sử dụng lại..")
        .build()
    );
  }

  @PreAuthorize("hasRole('ADMIN')")
  public BulkImportResult importUsers(MultipartFile file) {
    int total = 0, created = 0, skipped = 0;
    List<String> errors = new ArrayList<>();
    List<CreateOrganizationMemberRequest> bulkOrg = new ArrayList<>();
    List<CreateOrganizationMemberRequest> bulkBlock = new ArrayList<>();

    try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = wb.getSheetAt(0);

      // Header đề xuất (mới):
      // 0 username
      // 1 email
      // 2 firstName
      // 3 lastName
      // 4 displayName
      // 5 role(ADMIN|TEACHER|STUDENT)  -> role hệ thống (User.roles)
      // 6 organization                -> TÊN tổ chức (hoặc UUID để tương thích cũ)
      // 7 organizationMemberRole      -> Admin|Teacher|Student (mặc định Student)
      // 8 blockName                   -> VD: "10A1" hoặc "10A1#G10" để phân biệt khi trùng tên
      // 9 blockRole                   -> override vai trò ở block (optional)

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

        String orgRef = getString(r, 6);         // tên org hoặc UUID
        String orgMember = getString(r, 7);      // Admin|Teacher|Student
        String blockRaw = getString(r, 8);       // "name" hoặc "name#code"
        String blockRole = getString(r, 9);      // optional

        if (email == null || email.isBlank() || username == null ||
            username.isBlank()) {
          skipped++;
          errors.add("Row " + (i + 1) + ": missing username/email");
          continue;
        }
        if (userRepository.findByUsername(username).isPresent() ||
            userRepository.findByEmail(email).isPresent()) {
          skipped++;
          continue;
        }

        // B1: tạo user & role hệ thống
        Role sysRole = roleRepository
            .findById(roleName)
            .orElseGet(() -> roleRepository.save(
                Role.builder().name(roleName).description(roleName).build()));
        User user = userRepository.save(User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(tempPassword))
            .roles(Set.of(sysRole))
            .enabled(true)
            .build());

        // Gửi event đăng ký kèm profile
        UserCreationRequest ureq = UserCreationRequest.builder()
            .username(username).email(email)
            .firstName(firstName).lastName(lastName).displayName(display)
            .build();
        UserProfileCreationPayload profilePayload =
            userPayloadMapper.toUserProfileCreationPayloadFromUserCreationRequest(
                ureq);
        userEventProducer.publishRegisteredUserEvent(user, profilePayload);

        // B2: resolve org theo TÊN (hoặc UUID cũ)
        String orgId = null;
        if (orgRef != null && !orgRef.isBlank()) {
          if (orgRef.matches("^[0-9a-fA-F-]{36}$")) {
            orgId = orgRef;
          } else {
            try {
              var api =
                  organizationClient.internalResolveOrganizationByName(orgRef);
              orgId = api != null && api.getResult() != null ?
                  api.getResult().getId() : null;
            } catch (Exception ex) {
              errors.add(
                  "Row " + (i + 1) + ": cannot resolve organization by name '" +
                      orgRef + "'");
            }
          }
        }

        if (orgId != null) {
          bulkOrg.add(
              buildMembership(user.getId(), "Organization", orgId, orgMember));
        }

        // B3: nếu có block -> resolve theo TÊN (có hỗ trợ '#code' để xử lý trùng)
        if (orgId != null && blockRaw != null && !blockRaw.isBlank()) {
          String name = blockRaw;
          String code = null;
          int idx = blockRaw.indexOf('#');
          if (idx > 0) {
            name = blockRaw.substring(0, idx).trim();
            code = blockRaw.substring(idx + 1).trim();
          }

          try {
            var api = organizationClient.internalResolveBlockByName(orgId, name,
                code);
            String blockId = api != null && api.getResult() != null ?
                api.getResult().getId() : null;
            if (blockId != null && !blockId.isBlank()) {
              bulkBlock.add(buildMembership(user.getId(), "Grade", blockId,
                  (blockRole == null || blockRole.isBlank()) ? orgMember :
                      blockRole));
            }
          } catch (Exception ex) {
            // 2 case phổ biến: không tìm thấy hoặc trùng tên (server ném INVALID_REQUEST_MEMBER)
            errors.add("Row " + (i + 1) + ": resolve block '" + blockRaw
                + "' failed (hint: dùng 'name#code' nếu bị trùng)");
          }
        }

        created++;
      }

      // B4: gọi bulk add
      if (!bulkOrg.isEmpty()) {
        organizationClient.bulkAddToOrg(resolveOrgIdFromList(bulkOrg),
            mapToBulk(bulkOrg));
      }
      if (!bulkBlock.isEmpty()) {
        organizationClient.bulkAddToBlock(resolveBlockIdFromList(bulkBlock),
            mapToBulk(bulkBlock));
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return new BulkImportResult(total, created, skipped, errors);
  }

  // helper

  private String resolveOrgIdFromList(
      List<CreateOrganizationMemberRequest> list) {
    return list.getFirst().getScopeId(); // giả định 1 file import cho 1 org
  }

  private String resolveBlockIdFromList(
      List<CreateOrganizationMemberRequest> list) {
    return list.getFirst().getScopeId();
  }

  private BulkAddMembersRequest mapToBulk(
      List<CreateOrganizationMemberRequest> list) {
    var items = list.stream().map(req ->
        MemberInfo.builder()
            .userId(req.getUserId())
            .role(req.getRole())
            .active(req.isActive())
            .build()
    ).toList();
    return BulkAddMembersRequest.builder()
        .members(items)
        .active(true)
        .build();
  }

  private CreateOrganizationMemberRequest buildMembership(
      String userId, String scopeType, String scopeId, String roleText) {

    // Chuẩn hóa role cho OrganizationService v2
    String normalized =
        (roleText == null || roleText.isBlank()) ? "STUDENT" : roleText.trim();
    if (normalized.equalsIgnoreCase("ADMIN")) {
      normalized = "ADMIN";
    }
    if (normalized.equalsIgnoreCase("TEACHER")) {
      normalized = "TEACHER";
    }
    if (normalized.equalsIgnoreCase("STUDENT")) {
      normalized = "STUDENT";
    }

    CreateOrganizationMemberRequest req = new CreateOrganizationMemberRequest();
    req.setUserId(userId);
    req.setScopeType(scopeType); // Organization|Grade
    req.setScopeId(scopeId);
    req.setRole(normalized);
    req.setActive(true);
    return req;
  }

  @PreAuthorize("hasRole('ADMIN')")
  public void exportOrgBlocksAndMembers(
      String orgRef,
      HttpServletResponse response) {
    try (Workbook wb = new XSSFWorkbook()) {

      // 1) Resolve orgId từ tên hoặc UUID
      String orgId;
      String orgName;
      if (orgRef == null || orgRef.isBlank()) {
        throw new IllegalArgumentException("orgRef is required");
      }
      if (orgRef.matches("^[0-9a-fA-F-]{36}$")) {
        orgId = orgRef;
        var api = organizationClient.internalResolveOrganizationByName(
            orgRef);
        orgName = (api != null && api.getResult() != null) ?
            api.getResult().getName() : orgRef;
      } else {
        var api = organizationClient.internalResolveOrganizationByName(orgRef);
        if (api == null || api.getResult() == null) {
          throw new IllegalArgumentException(
              "Cannot resolve organization: " + orgRef);
        }
        orgId = api.getResult().getId();
        orgName = api.getResult().getName();
      }

      // 2) Gọi internalGetBlocksOfOrg để lấy toàn bộ block (+ “Unassigned” nếu có)
      var blocksApi = organizationClient.internalGetBlocksOfOrg(
          orgId, 1, 5000, 1, 5000, true, true);
      var page = blocksApi != null ? blocksApi.getResult() : null;
      var blocks = (page != null && page.getData() != null) ? page.getData() :
          List.<com.codecampus.identity.dto.response.org.BlockWithMembersLite>of();

      // 3) Tạo sheet và header
      Sheet sheet = wb.createSheet("org-block-member");
      Row header = sheet.createRow(0);
      String[] headers = {
          "orgName", "orgId", "blockName", "blockCode", "blockId",
          "userId", "username", "email",
          "memberRole", "active"
      };
      for (int c = 0; c < headers.length; c++) {
        header.createCell(c).setCellValue(headers[c]);
      }

      // 4) Ghi từng member (join userId -> username, email từ identity DB)
      int rowIdx = 1;
      for (var b : blocks) {
        String blockId = b.getId();
        String blockName = b.getName();
        String blockCode = b.getCode();

        var membersPage = b.getMembers();
        if (membersPage == null || membersPage.getData() == null ||
            membersPage.getData().isEmpty()) {
          // vẫn ghi 1 dòng block rỗng (tuỳ ý)
          continue;
        }

        for (var m : membersPage.getData()) {
          Row r = sheet.createRow(rowIdx++);
          r.createCell(0).setCellValue(orgName);
          r.createCell(1).setCellValue(orgId);
          r.createCell(2).setCellValue(blockName != null ? blockName : "");
          r.createCell(3).setCellValue(blockCode != null ? blockCode : "");
          r.createCell(4).setCellValue(blockId != null ? blockId : "");

          String uid = m.getUserId();
          r.createCell(5).setCellValue(uid);

          // load nhẹ thông tin user từ identity
          User u = null;
          if (uid != null) {
            u = userRepository.findById(uid).orElse(null);
          }
          r.createCell(6).setCellValue(
              u != null && u.getUsername() != null ? u.getUsername() : "");
          r.createCell(7).setCellValue(
              u != null && u.getEmail() != null ? u.getEmail() : "");

          r.createCell(8).setCellValue(m.getRole() != null ? m.getRole() : "");
          r.createCell(9).setCellValue(m.isActive());
        }
      }

      // 5) xuất file
      response.setContentType(
          "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setHeader("Content-Disposition",
          "attachment; filename=org-block-member.xlsx");
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
