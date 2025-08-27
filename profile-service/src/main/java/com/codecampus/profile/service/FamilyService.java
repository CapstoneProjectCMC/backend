package com.codecampus.profile.service;

import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.exception.AppException;
import com.codecampus.profile.exception.ErrorCode;
import com.codecampus.profile.repository.UserProfileRepository;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service xử lý quan hệ gia đình giữa các UserProfile,
 * bao gồm thêm/xóa con và truy vấn danh sách con của một phụ huynh.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FamilyService {
  UserProfileRepository userProfileRepository;

  UserProfileService userProfileService;

  @Transactional
  public void addChild(String parentId, String childId) {
    // đảm bảo tồn tại
    userProfileService.getUserProfile(parentId);
    userProfileService.getUserProfile(childId);
    userProfileRepository.mergeParentChild(parentId, childId);
  }

  @Transactional
  public void removeChild(String parentId, String childId) {
    userProfileRepository.deleteParentChild(parentId, childId);
  }


  /**
   * Lấy danh sách các UserProfile con của phụ huynh.
   * <p>
   * Truy vấn {@link UserProfileRepository} theo {@code parentId},
   * sao chép danh sách con và trả về dưới dạng immutable list.
   *
   * @param parentId ID của người dùng đóng vai trò phụ huynh
   * @return danh sách {@link UserProfile} con của phụ huynh
   * @throws AppException với {@link ErrorCode#USER_NOT_FOUND}
   *                      nếu không tìm thấy profile của phụ huynh
   */
  public List<UserProfile> getChildren(String parentId) {
    return userProfileRepository
        .findByUserId(parentId)
        .map(p -> List.copyOf(p.getChildren()))
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
  }
}
