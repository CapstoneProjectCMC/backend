package com.codecampus.ai.controller;

import com.codecampus.ai.dto.common.ApiResponse;
import com.codecampus.ai.dto.request.ChatRequest;
import com.codecampus.ai.dto.request.chat.CreateThreadRequest;
import com.codecampus.ai.dto.request.chat.RenameThreadRequest;
import com.codecampus.ai.dto.response.StoredFile;
import com.codecampus.ai.dto.response.chat.ThreadDetailResponse;
import com.codecampus.ai.dto.response.chat.ThreadResponse;
import com.codecampus.ai.service.ChatMessageService;
import com.codecampus.ai.service.ChatService;
import com.codecampus.ai.service.ChatThreadService;
import com.codecampus.ai.service.FileStorageService;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/chat")
public class ChatThreadController {
  ChatThreadService chatThreadService;
  ChatMessageService chatMessageService;
  ChatService chatService;
  FileStorageService fileStorageService;

  @GetMapping("/threads")
  ApiResponse<List<ThreadResponse>> myThreads() {
    return ApiResponse.<List<ThreadResponse>>builder()
        .result(chatThreadService.myThreads())
        .message("Danh sách threads của bạn")
        .build();
  }

  @GetMapping("/thread/{id}")
  ApiResponse<ThreadDetailResponse> getThread(
      @PathVariable String id) {
    return ApiResponse.<ThreadDetailResponse>builder()
        .result(chatThreadService.getThread(id))
        .message("Thông tin thread + lịch sử messages")
        .build();
  }

  @PostMapping("/thread")
  ApiResponse<ThreadResponse> createThread(
      @RequestBody CreateThreadRequest req) {
    return ApiResponse.<ThreadResponse>builder()
        .result(chatThreadService.createThread(
            req == null ? null : req.title()))
        .message("Tạo thread thành công")
        .build();
  }

  @PatchMapping("/thread/{id}")
  ApiResponse<ThreadResponse> renameThread(
      @PathVariable String id,
      @RequestBody RenameThreadRequest req) {
    return ApiResponse.<ThreadResponse>builder()
        .result(chatThreadService.renameThread(id, req.title()))
        .message("Đổi tên thread thành công")
        .build();
  }

  @DeleteMapping("/thread/{id}")
  ApiResponse<Void> deleteThread(
      @PathVariable String id) {
    chatThreadService.delete(id);
    return ApiResponse.<Void>builder()
        .message(
            "Đã xoá thread (đồng thời xoá lịch sử chat của thread)")
        .build();
  }

  @PostMapping("/thread/{id}/messages")
  ApiResponse<String> sendChat(
      @PathVariable String id,
      @RequestBody ChatRequest request) {
    // Lưu message USER
    chatMessageService.addUserMessage(id, request.message());

    // Gọi AI
    String content = chatService.chat(id, request);

    // Lưu message ASSISTANT
    chatMessageService.addAssistantMessage(id, content);

    // Cập nhật lastMessageAt
    chatThreadService.touchThread(id);
    return ApiResponse.<String>builder()
        .result(content)
        .message("Kết quả chat với AI (thread)")
        .build();
  }

  @PostMapping("/thread/{id}/messages-with-image")
  ApiResponse<String> sendChatWithImage(
      @PathVariable String id,
      @RequestParam("file") MultipartFile file,
      @RequestParam("message") String message) {

    // 1) Lưu file vật lý + tạo URL public
    StoredFile stored = fileStorageService.store(file);

    // 2) Lưu message USER + metadata ảnh (bao gồm imageUrl)
    chatMessageService.addUserMessageWithImage(
        id,
        message,
        stored.originalName(),
        stored.contentType(),
        stored.publicUrl()
    );

    // 3) Gọi AI với ảnh từ file đã lưu
    String content = chatService.chatWithImage(
        id,
        stored.absolutePath(),
        stored.contentType(),
        message
    );

    // 4) Lưu message ASSISTANT
    chatMessageService.addAssistantMessage(id, content);

    // 5) Update lastMessageAt
    chatThreadService.touchThread(id);

    return ApiResponse.<String>builder()
        .result(content)
        .message("Kết quả chat với AI (thread, có ảnh)")
        .build();
  }
}
