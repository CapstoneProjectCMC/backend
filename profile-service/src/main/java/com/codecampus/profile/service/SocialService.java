package com.codecampus.profile.service;

import static com.codecampus.profile.helper.PageResponseHelper.toPageResponse;

import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.properties.social.Blocks;
import com.codecampus.profile.entity.properties.social.Follows;
import com.codecampus.profile.helper.AuthenticationHelper;
import com.codecampus.profile.repository.UserProfileRepository;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Transactional
  public void follow(String targetUserId) {
    // đảm bảo cả 2 user hợp lệ/active (re-use logic cũ)
    userProfileService.getUserProfile();           // me
    userProfileService.getUserProfile(targetUserId); // target
    userProfileRepository.mergeFollow(
        AuthenticationHelper.getMyUserId(), targetUserId, Instant.now());
  }

  @Transactional
  public void unfollow(String targetUserId) {
    userProfileRepository.deleteFollow(
        AuthenticationHelper.getMyUserId(), targetUserId);
  }

  @Transactional
  public void block(String targetUserId) {
    userProfileService.getUserProfile();             // me
    userProfileService.getUserProfile(targetUserId); // target
    userProfileRepository.mergeBlock(
        AuthenticationHelper.getMyUserId(), targetUserId, Instant.now());
  }

  @Transactional
  public void unblock(String targetUserId) {
    userProfileRepository.deleteBlock(
        AuthenticationHelper.getMyUserId(), targetUserId);
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
        .findFollowers(AuthenticationHelper.getMyUserId(), pageable);

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
        .findFollowings(AuthenticationHelper.getMyUserId(), pageable);

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
        .findBlocked(AuthenticationHelper.getMyUserId(), pageable);

    return toPageResponse(pageData, page);
  }
}
