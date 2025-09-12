package com.codecampus.organization.helper;

import com.codecampus.organization.constant.file.FileType;
import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.response.UploadedFileResponse;
import com.codecampus.organization.repository.client.FileClient;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationHelper {

  FileClient fileClient;

  public String uploadIfAny(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }
    ApiResponse<UploadedFileResponse> api = fileClient.uploadImage(
        file, FileType.Image, "org-logo", List.of("logo", "organization")
    );
    if (api == null || api.getResult() == null ||
        api.getResult().getUrl() == null) {
      throw new IllegalStateException("Upload file failed: invalid response");
    }
    return api.getResult().getUrl();
  }
}
