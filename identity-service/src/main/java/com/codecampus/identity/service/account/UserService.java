package com.codecampus.identity.service.account;

import com.codecampus.identity.dto.common.PageResponse;
import com.codecampus.identity.dto.request.authentication.PasswordCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.authentication.UserUpdateRequest;
import com.codecampus.identity.dto.response.authentication.UserResponse;
import com.codecampus.identity.entity.account.Role;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.helper.AuthenticationHelper;
import com.codecampus.identity.helper.ProfileSyncHelper;
import com.codecampus.identity.mapper.authentication.UserMapper;
import com.codecampus.identity.mapper.client.UserProfileMapper;
import com.codecampus.identity.mapper.kafka.UserPayloadMapper;
import com.codecampus.identity.repository.account.RoleRepository;
import com.codecampus.identity.repository.account.UserRepository;
import com.codecampus.identity.repository.httpclient.profile.ProfileClient;
import com.codecampus.identity.service.authentication.OtpService;
import com.codecampus.identity.service.kafka.UserEventProducer;
import events.user.data.UserProfileCreationPayload;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;

import static com.codecampus.identity.constant.authentication.AuthenticationConstant.USER_ROLE;
import static com.codecampus.identity.helper.PageResponseHelper.toPageResponse;

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
@Builder
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
    AuthenticationHelper authenticationHelper;
    ProfileSyncHelper profileSyncHelper;
    UserEventProducer userEventProducer;

    /**
     * Tạo mới người dùng, gán vai trò USER và khởi tạo profile.
     *
     * <p>Chỉ ADMIN được phép gọi.
     * - Kiểm tra tồn tại username và email.
     * - Mã hóa mật khẩu.
     * - Gán vai trò mặc định.
     * - Lưu User và tạo profile qua ProfileClient.
     * </p>
     *
     * @param request thông tin tạo người dùng mới
     * @throws AppException nếu user đã tồn tại
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void createUser(UserCreationRequest request) {
        authenticationHelper.checkExistsUsernameEmail(
                request.getUsername(),
                request.getEmail()
        );

        User user = userMapper.toUserFromUserCreationRequest(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(USER_ROLE)
                .ifPresent(roles::add);
        user.setRoles(roles);
        user.setEnabled(true);

        try {
            user = userRepository.save(user);
            userEventProducer.publishCreatedUserEvent(user);
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
        User user = findUser(AuthenticationHelper.getMyUserId());

        if (StringUtils.hasText(request.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_ALREADY_EXISTS);
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    /**
     * Lấy thông tin của chính người dùng đang đăng nhập.
     *
     * @return UserResponse chứa thông tin người dùng
     */
    public UserResponse getMyInfo() {
        return getUser(AuthenticationHelper.getMyUserId());
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
        User user = findUser(userId);
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
        User user = findUser(AuthenticationHelper.getMyUserId());
        updateUser(request, user);
    }

    private void updateUser(
            UserUpdateRequest request,
            User user) {
        userMapper.updateUserUpdateRequestToUser(user, request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

//    List<Role> roles = roleRepository.findAllById(request.getRoles());
//    user.setRoles(new HashSet<>(roles));
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
        User user = findUser(userId);
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

    /**
     * Lấy danh sách người dùng với phân trang.
     *
     * <p>Chỉ ADMIN được phép gọi.</p>
     *
     * @param page số trang (bắt đầu từ 1)
     * @param size kích thước trang
     * @return PageResponse chứa danh sách UserResponse và thông tin phân trang
     */
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> getUsers(
            int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UserResponse> pageData = userRepository
                .findAll(pageable)
                .map(userMapper::toUserResponseFromUser);

        return toPageResponse(pageData, page);
    }

    /**
     * Lấy thông tin người dùng theo ID.
     *
     * @param userId ID người dùng
     * @return UserResponse chứa thông tin người dùng
     * @throws AppException nếu không tìm thấy
     */
    public UserResponse getUser(String userId) {
        return userMapper.toUserResponseFromUser(
                userRepository.findById(userId)
                        .orElseThrow(() -> new AppException(
                                ErrorCode.USER_NOT_FOUND))
        );
    }

    /**
     * Tìm entity User theo ID.
     *
     * @param id ID người dùng
     * @return User entity
     * @throws AppException nếu không tìm thấy
     */
    public User findUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public String getRoleName(User user) {
        return user.getRoles().stream()
                .findFirst()
                .map(Role::getName)
                .orElse(null);
    }
}
