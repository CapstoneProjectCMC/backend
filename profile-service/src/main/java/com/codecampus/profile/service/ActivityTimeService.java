package com.codecampus.profile.service;

import static com.codecampus.profile.utils.PageResponseUtils.toPageResponse;

import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.ActivityWeek;
import com.codecampus.profile.repository.UserProfileRepository;
import com.codecampus.profile.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service cung cấp các API liên quan đến thông tin hoạt động (Activity)
 * của người dùng, ví dụ như thống kê theo tuần.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActivityTimeService {

  UserProfileRepository userProfileRepository;

  /**
   * Lấy danh sách phân trang các {@link ActivityWeek} của người dùng hiện tại.
   * <p>
   * Phương thức sẽ sử dụng {@link SecurityUtils#getMyUserId()} để xác định
   * ID người dùng, sau đó gọi repository tương ứng để truy vấn dữ liệu
   * theo tuần. Kết quả trả về được bao gói trong DTO {@link PageResponse}.
   *
   * @param page số trang hiện tại (bắt đầu từ 1)
   * @param size số phần tử trên mỗi trang
   * @return {@link PageResponse} chứa danh sách đối tượng {@link ActivityWeek}
   * đã được phân trang, bao gồm các thông tin:
   * <ul>
   *   <li>currentPage: trang đang trả về</li>
   *   <li>pageSize: số phần tử mỗi trang</li>
   *   <li>totalPages: tổng số trang</li>
   *   <li>totalElements: tổng số phần tử</li>
   *   <li>data: danh sách {@link ActivityWeek} ở trang này</li>
   * </ul>
   */
  public PageResponse<ActivityWeek> getActivityWeeks(
      int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findActivityWeek(SecurityUtils.getMyUserId(), pageable);

    return toPageResponse(pageData, page);
  }
}
