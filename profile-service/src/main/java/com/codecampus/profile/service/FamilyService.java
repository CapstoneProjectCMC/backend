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

/**
 * Service xử lý quan hệ gia đình giữa các UserProfile,
 * bao gồm thêm/xóa con và truy vấn danh sách con của một phụ huynh.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FamilyService
{
  UserProfileRepository userProfileRepository;

  UserProfileService userProfileService;

  /**
   * Thêm một UserProfile làm con cho phụ huynh đã cho.
   * <p>
   * Lấy {@link UserProfile} của phụ huynh theo {@code parentId} và
   * của con theo {@code childId}, sau đó thêm con vào danh sách
   * {@code parent.getChildren()} và lưu lại.
   *
   * @param parentId ID của người dùng đóng vai trò phụ huynh
   * @param childId  ID của người dùng đóng vai trò con
   * @throws AppException với {@link ErrorCode#USER_NOT_FOUND}
   *         nếu không tìm thấy profile của phụ huynh hoặc của con
   */
  public void addChild(String parentId, String childId) {
    UserProfile parent = userProfileService.getUserProfile(parentId);
    UserProfile child = userProfileService.getUserProfile(childId);

    parent.getChildren().add(child);
    userProfileRepository.save(parent);
  }


  /**
   * Loại bỏ một UserProfile khỏi danh sách con của phụ huynh.
   * <p>
   * Lấy {@link UserProfile} của phụ huynh theo {@code parentId},
   * sau đó loại bỏ mọi {@code child} có ID bằng {@code childId}
   * khỏi {@code parent.getChildren()} và lưu lại.
   *
   * @param parentId ID của người dùng đóng vai trò phụ huynh
   * @param childId  ID của người dùng cần loại khỏi danh sách con
   * @throws AppException với {@link ErrorCode#USER_NOT_FOUND}
   *         nếu không tìm thấy profile của phụ huynh
   */
  public void removeChild(String parentId, String childId)
  {
    UserProfile parent = userProfileService.getUserProfile(parentId);
    parent.getChildren().removeIf(child -> childId.equals(child.getId()));
    userProfileRepository.save(parent);
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
   *         nếu không tìm thấy profile của phụ huynh
   */
  public List<UserProfile> getChildren(String parentId) {
    return userProfileRepository
        .findByUserId(parentId)
        .map(p -> List.copyOf(p.getChildren()))
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
  }
}
