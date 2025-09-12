package com.codecampus.chat.controller;

import com.codecampus.chat.dto.common.ApiResponse;
import com.codecampus.chat.dto.common.PageResponse;
import com.codecampus.chat.dto.request.ConversationRequest;
import com.codecampus.chat.dto.request.CreateGroupForm;
import com.codecampus.chat.dto.request.GroupUpdateRequest;
import com.codecampus.chat.dto.request.MarkReadRequest;
import com.codecampus.chat.dto.request.MembersUpdateRequest;
import com.codecampus.chat.dto.request.MuteRequest;
import com.codecampus.chat.dto.request.RoleUpdateRequest;
import com.codecampus.chat.dto.request.UpdateGroupMetaForm;
import com.codecampus.chat.dto.response.ConversationResponse;
import com.codecampus.chat.service.ConversationService;
import com.codecampus.chat.service.GroupImageService;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationController {

  ConversationService conversationService;
  GroupImageService groupImageService;

  @PostMapping("/conversation")
  ApiResponse<Void> createConversation(
      @RequestBody @Valid ConversationRequest request) {
    conversationService.createConversation(request);
    return ApiResponse.<Void>builder()
        .message("Tạo hội thoại thành công!")
        .build();
  }

  // === TẠO GROUP ===
  @PostMapping(
      value = "/conversation/group",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  ApiResponse<Void> createGroup(
      @Valid @ModelAttribute CreateGroupForm form
  ) {
    String avatarUrl = null;
    if (form.getFileAvatarGroup() != null &&
        !form.getFileAvatarGroup().isEmpty()) {
      avatarUrl =
          groupImageService.uploadGroupAvatar(form.getFileAvatarGroup());
    }

    GroupUpdateRequest meta =
        GroupUpdateRequest.builder()
            .name(form.getName())
            .avatarUrl(avatarUrl)
            .topic(form.getTopic())
            .build();

    ConversationRequest conversationRequest = new ConversationRequest(
        form.getType() == null ? "GROUP" : form.getType(),
        form.getParticipantIds()
    );

    conversationService.createGroup(conversationRequest, meta);
    return ApiResponse.<Void>builder()
        .message("Tạo nhóm hội thoại thành công!")
        .build();
  }

  // === SỬA META NHÓM ===
  @PatchMapping(
      value = "/conversation/group/{groupId}/meta",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  ApiResponse<Void> updateGroupMeta(
      @PathVariable("groupId") String groupId,
      @ModelAttribute UpdateGroupMetaForm form) {

    String avatarUrl = null;
    if (form.getFileAvatarGroup() != null &&
        !form.getFileAvatarGroup().isEmpty()) {
      avatarUrl =
          groupImageService.uploadGroupAvatar(form.getFileAvatarGroup());
    }

    GroupUpdateRequest meta =
        GroupUpdateRequest.builder()
            .name(form.getName())
            .avatarUrl(avatarUrl)
            .topic(form.getTopic())
            .build();

    conversationService.updateGroupMeta(groupId, meta);
    return ApiResponse.<Void>builder()
        .message("Sửa meta nhóm hội thoại thành công!")
        .build();
  }

  // === THÊM/XOÁ MEMBER ===
  @PostMapping("/conversation/group/{groupId}/members")
  ApiResponse<Void> addMembers(
      @PathVariable("groupId") String groupId,
      @RequestBody @Valid MembersUpdateRequest request) {
    conversationService.addMembers(
        groupId,
        request.getUserIds());
    return ApiResponse.<Void>builder()
        .message("Thêm member vào nhóm thành công!")
        .build();
  }

  @DeleteMapping("/conversation/group/{groupId}/members")
  ApiResponse<Void> removeMembers(
      @PathVariable("groupId") String groupId,
      @RequestBody @Valid MembersUpdateRequest request) {
    conversationService.removeMembers(groupId, request.getUserIds());
    return ApiResponse.<Void>builder()
        .message("Xoá member khỏi nhóm thành công!")
        .build();
  }

  // === QUYỀN ===
  @PostMapping("/conversation/group/{groupId}/role")
  ApiResponse<Void> setRole(
      @PathVariable("groupId") String groupId,
      @RequestBody RoleUpdateRequest request) {
    conversationService.setRole(groupId, request.getUserId(),
        request.getRole());
    return ApiResponse.<Void>builder()
        .message("Set quyền cho member thành công!")
        .build();
  }

  // === RỜI/GIẢI TÁN ===
  @PostMapping("/conversation/group/{groupId}/leave")
  ApiResponse<Void> leave(
      @PathVariable("groupId") String groupId) {
    conversationService.leaveGroup(groupId);
    return ApiResponse.<Void>builder()
        .message("Rời nhóm thành công!")
        .build();
  }

  @DeleteMapping("/conversation/group/{groupId}")
  ApiResponse<Void> deleteGroup(
      @PathVariable("groupId") String id) {
    conversationService.deleteGroup(id);
    return ApiResponse.<Void>builder()
        .message("Xoá nhóm thành công!")
        .build();
  }

  // === MUTE ===
  @PostMapping("/conversation/group/{groupId}/mute")
  ApiResponse<Void> mute(
      @PathVariable("groupId") String groupId,
      @RequestBody MuteRequest request) {
    conversationService.setMute(groupId, request.getMutedUntil());
    return ApiResponse.<Void>builder()
        .message("Mute bản thân thành công!")
        .build();
  }

  @DeleteMapping("/conversation/group/{groupId}/mute")
  ApiResponse<Void> unmute(
      @PathVariable("groupId") String groupId) {
    conversationService.unmute(groupId);
    return ApiResponse.<Void>builder()
        .message("Unmute bản thân thành công!")
        .build();
  }

  @PostMapping("/conversation/group/{groupId}/member/{userId}/mute")
  ApiResponse<Void> muteMember(
      @PathVariable String groupId,
      @PathVariable String userId,
      @RequestBody(required = false) MuteRequest request) {
    Instant until = (request != null ? request.getMutedUntil() :
        Instant.now().plusSeconds(3600)); // ví dụ default 1h
    conversationService.setMemberMute(groupId, userId, until);
    return ApiResponse.<Void>builder()
        .message("Mute member thành công!")
        .build();
  }

  @DeleteMapping("/conversation/group/{groupId}/member/{userId}/mute")
  ApiResponse<Void> unmuteMember(
      @PathVariable String groupId,
      @PathVariable String userId) {
    conversationService.unMemberMute(groupId, userId);
    return ApiResponse.<Void>builder()
        .message("Unmute member thành công!")
        .build();
  }

  // === ĐÁNH DẤU ĐÃ ĐỌC ===
  @PostMapping("/conversation/{conversationId}/read")
  ApiResponse<Void> markRead(
      @PathVariable("conversationId") String conversationId,
      @RequestBody(required = false) MarkReadRequest request) {
    Instant upTo = (request != null ? request.getUpToTime() : null);
    String msgId = (request != null ? request.getUpToMessageId() : null);
    conversationService.markRead(conversationId, msgId, upTo);
    return ApiResponse.<Void>builder()
        .message("Đánh dấu đã đọc thành công!")
        .build();
  }

  @GetMapping("/conversations")
  ApiResponse<PageResponse<ConversationResponse>> getMyConversations(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    return ApiResponse.<PageResponse<ConversationResponse>>builder()
        .message("Get toàn bộ cuộc hội thoại của bản thân thành công!")
        .result(conversationService.getMyConversations(page, size))
        .build();
  }

  @GetMapping("/conversation/{id}")
  ApiResponse<ConversationResponse> getConversationById(
      @PathVariable("id") String id) {
    return ApiResponse.<ConversationResponse>builder()
        .message("Lấy thông tin hội thoại thành công!")
        .result(conversationService.getConversationById(id))
        .build();
  }
}
