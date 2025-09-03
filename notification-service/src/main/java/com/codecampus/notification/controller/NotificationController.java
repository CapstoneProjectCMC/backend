package com.codecampus.notification.controller;

import com.codecampus.notification.dto.common.ApiResponse;
import com.codecampus.notification.dto.common.PageResponse;
import com.codecampus.notification.dto.response.NotificationView;
import com.codecampus.notification.helper.AuthenticationHelper;
import com.codecampus.notification.service.NotificationService;
import com.codecampus.notification.service.NotificationStatusService;
import java.time.Instant;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class NotificationController {

  NotificationStatusService notificationStatusService;
  NotificationService notificationService;

  @GetMapping("/my")
  public ApiResponse<PageResponse<NotificationView>> getAllMyNotifications(
      @RequestParam(required = false, defaultValue = "1") int page,
      @RequestParam(required = false, defaultValue = "20") int size,
      @RequestParam(required = false, defaultValue = "ALL") String readStatus,
      // ALL|READ|UNREAD
      @RequestParam(required = false) Instant from,
      @RequestParam(required = false) Instant to
  ) {
    String me = AuthenticationHelper.getMyUserId();
    return ApiResponse.<PageResponse<NotificationView>>builder()
        .message("Get thông báo của bản thân thành công!")
        .result(notificationService.getMyNotifications(
            me,
            page, size,
            readStatus,
            from, to
        ))
        .build();
  }

  /**
   * Lấy chi tiết 1 notification (của tôi)
   */
  @GetMapping("/{id}")
  public ApiResponse<NotificationView> getMyNotification(
      @PathVariable String id) {
    String me = AuthenticationHelper.getMyUserId();
    return ApiResponse.<NotificationView>builder()
        .message("Get một thông báo của bản thân thành công!")
        .result(notificationService.getNotification(me, id))
        .build();
  }

  /**
   * Đếm số UNREAD (badge)
   */
  @GetMapping("/my/unread-count")
  public ApiResponse<Long> countMyUnread() {
    String me = AuthenticationHelper.getMyUserId();
    return ApiResponse.<Long>builder()
        .message("Đếm số thông báo chưa đọc!")
        .result(notificationService.countMyUnread(me))
        .build();
  }


  @PostMapping("/my/mark-read")
  ApiResponse<Long> markMyRead(
      @RequestBody Set<String> ids) {
    String me = AuthenticationHelper.getMyUserId();
    return ApiResponse.<Long>builder()
        .message("Mark read thành công!")
        .result(notificationStatusService.markRead(me, ids, Instant.now()))
        .build();
  }

  @PostMapping("/my/mark-unread")
  ApiResponse<Long> markMyUnread(
      @RequestBody Set<String> ids) {
    String me = AuthenticationHelper.getMyUserId();
    return ApiResponse.<Long>builder()
        .message("Mark unread thành công!")
        .result(notificationStatusService.markUnread(me, ids))
        .build();
  }

  @PostMapping("/my/mark-all-read")
  ApiResponse<Long> markMyAllRead(
      @RequestParam(required = false) Instant before) {
    String me = AuthenticationHelper.getMyUserId();
    return ApiResponse.<Long>builder()
        .message("Mark read hết thành công!")
        .result(
            notificationStatusService.markAllRead(me, before, Instant.now()))
        .build();
  }
}
