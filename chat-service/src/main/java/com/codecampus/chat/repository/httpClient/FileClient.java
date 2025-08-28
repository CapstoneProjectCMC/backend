package com.codecampus.chat.repository.httpClient;

import com.codecampus.chat.configuration.config.AuthenticationRequestInterceptor;
import com.codecampus.chat.configuration.config.FeignMultipartConfiguration;
import com.codecampus.chat.constant.file.FileType;
import com.codecampus.chat.dto.common.ApiResponse;
import com.codecampus.chat.dto.response.UploadedFileResponse;
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
        FeignMultipartConfiguration.class}
)
public interface FileClient {

  @PostMapping(value = "/api/FileDocument/add",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ApiResponse<UploadedFileResponse> uploadImage(
      @RequestPart("file") MultipartFile file,
      @RequestPart("category") FileType category,
      @RequestPart("description") String description,
      @RequestPart("tags") List<String> tags
  );
}