package com.codecampus.identity.utils;

public class SecurityUtils {
    //  /**
//   * Trả về đối tượng người dùng hiện đang được xác thực từ cơ sở dữ liệu hoặc null nếu không tìm thấy
//   * hoặc chưa đăng nhập.
//   *
//   * @param userRepository kho lưu trữ dùng để truy vấn dữ liệu người dùng
//   * @return đối tượng User hoặc null nếu không xác thực hoặc không tìm thấy
//   */
//  public static User getCurrentUser(
//      UserRepository userRepository) {
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//    if (authentication == null || !authentication.isAuthenticated()) {
//      return null;
//    }
//
//    Object principal = authentication.getPrincipal();
//
//    if (principal instanceof UserDetails) {
//      String username = ((UserDetails) principal).getUsername();
//      return userRepository.findByUsername(username)
//          .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//    }
//
//    if (principal instanceof User) {
//      return (User) principal;
//    }
//
//    return null;
//  }
}
