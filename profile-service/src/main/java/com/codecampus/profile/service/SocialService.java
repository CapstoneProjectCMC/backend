package com.codecampus.profile.service;

import static com.codecampus.profile.utils.PageResponseUtils.toPageResponse;

import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.entity.properties.social.Blocks;
import com.codecampus.profile.entity.properties.social.Follows;
import com.codecampus.profile.repository.UserProfileRepository;
import com.codecampus.profile.utils.SecurityUtils;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service xử lý các nghiệp vụ “xã hội” (social) của người dùng:
 * theo dõi (follow), bỏ theo dõi (unfollow), chặn (block), bỏ chặn (unblock),
 * cũng như truy vấn danh sách followers, followings và blocked users.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocialService {
  UserProfileRepository userProfileRepository;

  UserProfileService userProfileService;

  /**
   * Theo dõi (follow) một người dùng khác.
   * <p>
   * Lấy profile người dùng hiện tại và profile của target theo {@code targetUserId},
   * kiểm tra tránh duplicate, rồi thêm vào danh sách {@link Follows} của người dùng hiện tại.
   *
   * @param targetUserId ID của người dùng cần follow
   * @throws com.codecampus.profile.exception.AppException nếu không tìm thấy profile của người dùng hiện tại hoặc của targetUserId
   */
  public void follow(String targetUserId) {
    UserProfile myProfile = userProfileService.getUserProfile();

    UserProfile targetProfile = userProfileService.getUserProfile(targetUserId);

    // Tránh follow trùng
    boolean exists = myProfile.getFollows().stream()
        .anyMatch(
            follows -> targetUserId.equals(follows.getTarget().getUserId())
        );

    if (!exists) {
      Follows follows = Follows.builder()
          .since(Instant.now())
          .target(targetProfile)
          .build();
      myProfile.getFollows().add(follows);
      userProfileRepository.save(myProfile);
    }
  }

  /**
   * Bỏ theo dõi (unfollow) một người dùng đã follow trước đó.
   * <p>
   * Lấy profile người dùng hiện tại, loại bỏ mọi entry trong danh sách {@link Follows}
   * có target trùng với {@code targetUserId}, sau đó lưu lại profile.
   *
   * @param targetUserId ID của người dùng cần unfollow
   * @throws com.codecampus.profile.exception.AppException nếu không tìm thấy profile của người dùng hiện tại
   */
  public void unfollow(String targetUserId) {
    UserProfile myProfile = userProfileService.getUserProfile();

    myProfile.getFollows().removeIf(
        follows -> targetUserId.equals(follows.getTarget().getUserId())
    );

    userProfileRepository.save(myProfile);
  }

  /**
   * Chặn (block) một người dùng.
   * <p>
   * Lấy profile người dùng hiện tại và target, kiểm tra tránh block trùng,
   * rồi thêm mới entry {@link Blocks} và lưu lại profile.
   *
   * @param targetUserId ID của người dùng cần block
   * @throws com.codecampus.profile.exception.AppException nếu không tìm thấy profile của người dùng hiện tại hoặc của targetUserId
   */
  public void block(String targetUserId) {
    UserProfile myProfile = userProfileService.getUserProfile();

    UserProfile targetProfile = userProfileService.getUserProfile(targetUserId);

    boolean exists = myProfile.getBlocks().stream()
        .anyMatch(
            blocks -> targetUserId.equals(blocks.getTarget().getUserId())
        );

    if (!exists) {
      Blocks blocks = Blocks.builder()
          .since(Instant.now())
          .target(targetProfile)
          .build();
      myProfile.getBlocks().add(blocks);
      userProfileRepository.save(myProfile);
    }
  }

  /**
   * Bỏ chặn (unblock) một người dùng đã block trước đó.
   * <p>
   * Lấy profile người dùng hiện tại, loại bỏ mọi entry trong danh sách {@link Blocks}
   * có target trùng với {@code targetUserId}, sau đó lưu lại profile.
   *
   * @param targetUserId ID của người dùng cần unblock
   * @throws com.codecampus.profile.exception.AppException nếu không tìm thấy profile của người dùng hiện tại
   */
  public void unblock(String targetUserId) {
    UserProfile myProfile = userProfileService.getUserProfile();

    myProfile.getBlocks().removeIf(
        blocks -> targetUserId.equals(blocks.getTarget().getUserId())
    );

    userProfileRepository.save(myProfile);
  }

  /**
   * Lấy danh sách phân trang những người đang theo dõi (followers) người dùng hiện tại.
   *
   * @param page số trang hiện tại (bắt đầu từ 1)
   * @param size số phần tử trên mỗi trang
   * @return {@link PageResponse} chứa các đối tượng {@link Follows}
   */
  public PageResponse<Follows> getFollowers(
      int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findFollowers(SecurityUtils.getMyUserId(), pageable);

    return toPageResponse(pageData, page);
  }

  /**
   * Lấy danh sách phân trang những người mà người dùng hiện tại đang follow.
   *
   * @param page số trang hiện tại (bắt đầu từ 1)
   * @param size số phần tử trên mỗi trang
   * @return {@link PageResponse} chứa các đối tượng {@link Follows}
   */
  public PageResponse<Follows> getFollowings(
      int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findFollowings(SecurityUtils.getMyUserId(), pageable);

    return toPageResponse(pageData, page);
  }

  /**
   * Lấy danh sách phân trang những người dùng bị chặn (blocked) bởi người dùng hiện tại.
   *
   * @param page số trang hiện tại (bắt đầu từ 1)
   * @param size số phần tử trên mỗi trang
   * @return {@link PageResponse} chứa các đối tượng {@link Blocks}
   */
  public PageResponse<Blocks> findBlocked(
      int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findBlocked(SecurityUtils.getMyUserId(), pageable);

    return toPageResponse(pageData, page);
  }
}
