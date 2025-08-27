package com.codecampus.post.repository.httpClient;

import com.codecampus.post.config.FeignConfig.AuthenticationRequestInterceptor;
import com.codecampus.post.config.FeignConfig.FeignMultipartConfiguration;
import com.codecampus.post.dto.request.AddFileDocumentDto;
import com.codecampus.post.dto.response.AddFileResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
    name = "file-service",
    url = "${app.services.file}",
    configuration = {FeignMultipartConfiguration.class,
        AuthenticationRequestInterceptor.class})
public interface FileServiceClient {

  @PostMapping(
      value = "/api/FileDocument/add",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  AddFileResponseDto uploadFile(
      @ModelAttribute AddFileDocumentDto dto);
}
