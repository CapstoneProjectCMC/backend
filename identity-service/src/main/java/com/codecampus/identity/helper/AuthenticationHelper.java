package com.codecampus.identity.helper;

import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.repository.account.UserRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Tiện ích hỗ trợ xác thực liên quan đến người dùng.
 *
 * <p>Dùng để kiểm tra tính hợp lệ ban đầu khi thao tác với User,
 * như kiểm tra xem username hoặc email đã tồn tại hay chưa.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationHelper {
    UserRepository userRepository;

    /**
     * Trả về đối tượng người dùng hiện đang được xác thực từ cơ sở dữ liệu hoặc null nếu không tìm thấy
     * hoặc chưa đăng nhập.
     *
     * @param userRepository kho lưu trữ dùng để truy vấn dữ liệu người dùng
     * @return đối tượng User hoặc null nếu không xác thực hoặc không tìm thấy
     */
    public static User getCurrentUser(
            UserRepository userRepository) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(
                            () -> new AppException(ErrorCode.USER_NOT_FOUND));
        }

        if (principal instanceof User) {
            return (User) principal;
        }

        return null;
    }

    public static String getMyEmail() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        return (auth == null) ? null : auth.getName();
    }

    public static String getMyUsername() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof Jwt jwt) {
            // JwtAuthenticationToken giữ nguyên đối tượng Jwt làm principal,
            return jwt.getClaimAsString("username");
        }

        return null;
    }

    public static String getMyUserId() {
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof Jwt jwt) {
            // JwtAuthenticationToken giữ nguyên đối tượng Jwt làm principal,
            return jwt.getClaimAsString("userId");
        }

        return null;
    }

    public static String extractToken(String header) {
        return Optional.ofNullable(header)
                .filter(h -> h.toLowerCase().startsWith("bearer "))
                .map(h -> h.substring(7))
                .orElseThrow(
                        () -> new AppException(ErrorCode.INVALID_TOKEN)
                );
    }

    /**
     * Kiểm tra xem username hoặc email đã tồn tại trong hệ thống hay chưa.
     *
     * <p>Nếu username đã tồn tại, ném AppException với mã lỗi USER_ALREADY_EXISTS.
     * Nếu email đã tồn tại, ném AppException với mã lỗi EMAIL_ALREADY_EXISTS.</p>
     *
     * @param username tên đăng nhập cần kiểm tra
     * @param email    địa chỉ email cần kiểm tra
     * @throws AppException khi username hoặc email đã tồn tại
     */
    public void checkExistsUsernameEmail(String username, String email) {
        // Kiểm tra username và email đã tồn tại
        if (userRepository.existsByUsername(username)) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }
}
