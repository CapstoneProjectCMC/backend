package com.codecampus.chat.service;

import com.codecampus.chat.constant.file.FileType;
import com.codecampus.chat.dto.common.ApiResponse;
import com.codecampus.chat.dto.response.UploadedFileResponse;
import com.codecampus.chat.repository.httpClient.FileClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupImageService {
  private final FileClient fileClient;

  public String uploadGroupAvatar(MultipartFile file) {
    ApiResponse<UploadedFileResponse> api = fileClient.uploadImage(
        file, FileType.Image,
        "group_avatar",
        List.of("chat", "group", "avatar"));
    UploadedFileResponse response = (api != null ? api.getResult() : null);
    if (response == null || response.getUrl() == null) {
      throw new IllegalStateException("Upload avatar nhóm thất bại");
    }
    return response.getUrl();
  }
}