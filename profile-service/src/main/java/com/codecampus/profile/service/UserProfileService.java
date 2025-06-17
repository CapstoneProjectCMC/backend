package com.codecampus.profile.service;

import static com.codecampus.profile.utils.PageResponseUtils.toPageResponse;

import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.dto.request.UserProfileCreationRequest;
import com.codecampus.profile.dto.request.UserProfileUpdateRequest;
import com.codecampus.profile.dto.response.UserProfileResponse;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.exception.AppException;
import com.codecampus.profile.exception.ErrorCode;
import com.codecampus.profile.mapper.UserProfileMapper;
import com.codecampus.profile.repository.UserProfileRepository;
import com.codecampus.profile.utils.AuthenticationUtils;
import com.codecampus.profile.utils.SecurityUtils;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Dịch vụ quản lý hồ sơ người dùng (UserProfile) trong hệ thống.
 *
 * <p>Cung cấp các chức năng:
 * <ul>
 *   <li>createUserProfile: Tạo hồ sơ mới cho người dùng.</li>
 *   <li>getUserProfileByUserId: Lấy hồ sơ theo userId.</li>
 *   <li>getUserProfileById: Lấy hồ sơ theo id hồ sơ (chỉ ADMIN).</li>
 *   <li>getAllUserProfiles: Lấy danh sách hồ sơ với phân trang (chỉ ADMIN).</li>
 *   <li>getMyUserProfile: Lấy hồ sơ của người dùng đang đăng nhập.</li>
 *   <li>updateMyUserProfile: Cập nhật hồ sơ của người dùng đang đăng nhập.</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService
{
  UserProfileRepository userProfileRepository;

  UserProfileMapper userProfileMapper;
  AuthenticationUtils authenticationUtils;

  /**
   * Tạo hồ sơ người dùng mới.
   *
   * <p>Quy trình:
   * <ol>
   *   <li>Kiểm tra xem userId đã có hồ sơ chưa, nếu có ném AppException USER_ALREADY_EXISTS.</li>
   *   <li>Chuyển đổi DTO sang entity, gán thời điểm tạo.</li>
   *   <li>Lưu entity và trả về DTO kết quả.</li>
   * </ol>
   * </p>
   *
   * @param request thông tin tạo hồ sơ người dùng
   * @return đối tượng UserProfileResponse chứa dữ liệu hồ sơ vừa tạo
   * @throws AppException nếu user đã tồn tại hồ sơ
   */
  public UserProfileResponse createUserProfile(
      UserProfileCreationRequest request) {

    authenticationUtils.checkExistsUserid(request.getUserId());

    UserProfile userProfile = userProfileMapper.toUserProfile(request);
    userProfile.setCreatedAt(Instant.now());
    userProfile = userProfileRepository.save(userProfile);

    return userProfileMapper.toUserProfileResponse(userProfile);
  }

  /**
   * Lấy hồ sơ người dùng theo userId.
   *
   * @param userId ID của người dùng
   * @return UserProfileResponse nếu tìm thấy hồ sơ
   * @throws AppException nếu không tìm thấy hồ sơ
   */
  public UserProfileResponse getUserProfileByUserId(String userId) {
    return userProfileRepository
        .findByUserId(userId)
        .map(userProfileMapper::toUserProfileResponse)
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );
  }

  /**
   * Lấy hồ sơ theo id hồ sơ (chỉ ADMIN được phép).
   *
   * @param id ID của hồ sơ
   * @return UserProfileResponse
   * @throws AppException nếu không tìm thấy
   */
  @PreAuthorize("hasRole('ADMIN')")
  public UserProfileResponse getUserProfileById(String id)
  {
    return userProfileRepository
        .findById(id)
        .map(userProfileMapper::toUserProfileResponse)
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );
  }

  /**
   * Lấy danh sách tất cả hồ sơ với phân trang (chỉ ADMIN được phép).
   *
   * @param page số trang (bắt đầu từ 1)
   * @param size số phần tử mỗi trang
   * @return PageResponse chứa danh sách UserProfileResponse và thông tin phân trang
   */
  @PreAuthorize("hasRole('ADMIN')")
  public PageResponse<UserProfileResponse> getAllUserProfiles(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findAll(pageable)
        .map(userProfileMapper::toUserProfileResponse);

    return toPageResponse(pageData, page);
  }

  /**
   * Lấy hồ sơ của người dùng đang đăng nhập.
   *
   * @return UserProfileResponse của người dùng hiện tại
   */
  public UserProfileResponse getMyUserProfile()
  {
    return getUserProfileByUserId(SecurityUtils.getMyUserId());
  }

  /**
   * Lấy hồ sơ của người dùng.
   *
   * @return UserProfile của người dùng
   */
  public UserProfile getUserProfile(String userId) {
    return userProfileRepository
        .findByUserId(userId)
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );
  }

  /**
   * Lấy hồ sơ của người dùng đang đăng nhập.
   *
   * @return UserProfile của người dùng hiện tại đang đăng nhập
   */
  public UserProfile getUserProfile() {
    return userProfileRepository
        .findByUserId(SecurityUtils.getMyUserId())
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );
  }

  /**
   * Cập nhật hồ sơ của người dùng đang đăng nhập.
   *
   * <p>Quy trình:
   * <ol>
   *   <li>Lấy entity UserProfile theo userId, nếu không tìm thấy ném AppException USER_NOT_FOUND.</li>
   *   <li>Cập nhật thông tin theo request.</li>
   *   <li>Lưu lại và trả về DTO kết quả.</li>
   * </ol>
   * </p>
   *
   * @param request thông tin cập nhật hồ sơ
   * @return UserProfileResponse sau khi cập nhật
   * @throws AppException nếu không tìm thấy hồ sơ
   */
  public UserProfileResponse updateMyUserProfile(
      UserProfileUpdateRequest request) {

    UserProfile profile = getUserProfile();

    userProfileMapper.updateUserProfile(profile, request);
    return userProfileMapper.toUserProfileResponse(
        userProfileRepository.save(profile)
    );
  }


}
