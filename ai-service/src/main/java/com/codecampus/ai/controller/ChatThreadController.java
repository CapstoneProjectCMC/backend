package com.codecampus.ai.controller;

import com.codecampus.ai.dto.common.ApiResponse;
import com.codecampus.ai.dto.request.ChatRequest;
import com.codecampus.ai.dto.request.chat.CreateThreadRequest;
import com.codecampus.ai.dto.request.chat.RenameThreadRequest;
import com.codecampus.ai.dto.response.chat.ThreadResponse;
import com.codecampus.ai.service.ChatService;
import com.codecampus.ai.service.ChatThreadService;
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

import java.util.List;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/chat")
public class ChatThreadController {
    ChatThreadService chatThreadService;
    ChatService chatService;

    @GetMapping("/threads")
    ApiResponse<List<ThreadResponse>> myThreads() {
        return ApiResponse.<List<ThreadResponse>>builder()
                .result(chatThreadService.myThreads())
                .message("Danh sách threads của bạn")
                .build();
    }

    @PostMapping("/threads")
    ApiResponse<ThreadResponse> create(
            @RequestBody CreateThreadRequest req) {
        return ApiResponse.<ThreadResponse>builder()
                .result(chatThreadService.create(
                        req == null ? null : req.title()))
                .message("Tạo thread thành công")
                .build();
    }

    @PatchMapping("/threads/{id}")
    ApiResponse<ThreadResponse> rename(
            @PathVariable String id,
            @RequestBody RenameThreadRequest req) {
        return ApiResponse.<ThreadResponse>builder()
                .result(chatThreadService.rename(id, req.title()))
                .message("Đổi tên thread thành công")
                .build();
    }

    @DeleteMapping("/threads/{id}")
    ApiResponse<Void> delete(
            @PathVariable String id) {
        chatThreadService.delete(id);
        return ApiResponse.<Void>builder()
                .message(
                        "Đã xoá thread (đồng thời xoá lịch sử chat của thread)")
                .build();
    }

    @PostMapping("/threads/{id}/messages")
    ApiResponse<String> send(
            @PathVariable String id,
            @RequestBody ChatRequest request) {
        String content = chatService.chat(id, request);
        chatThreadService.touch(id);
        return ApiResponse.<String>builder()
                .result(content)
                .message("Kết quả chat với AI (thread)")
                .build();
    }

    @PostMapping("/threads/{id}/messages-with-image")
    ApiResponse<String> sendWithImage(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("message") String message) {
        String content = chatService.chatWithImage(id, file, message);
        chatThreadService.touch(id);
        return ApiResponse.<String>builder()
                .result(content)
                .message("Kết quả chat với AI (thread, có ảnh)")
                .build();
    }
}
