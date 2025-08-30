package com.codecampus.organization.repository.client;

import com.codecampus.organization.configuration.config.AuthenticationRequestInterceptor;
import com.codecampus.organization.configuration.config.FeignConfiguration;
import com.codecampus.organization.constant.file.FileType;
import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.response.UploadedFileResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
    name = "file-service",
    url = "${app.services.file}",
    configuration = {AuthenticationRequestInterceptor.class,
        FeignConfiguration.class}
)
public interface FileClient {
  @PostMapping(
      value = "/api/FileDocument/add",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ApiResponse<UploadedFileResponse> uploadImage(
      @RequestPart("file") MultipartFile file,
      @RequestPart("category") FileType category,
      @RequestPart("description") String description,
      @RequestPart("tags") List<String> tags
  );
}