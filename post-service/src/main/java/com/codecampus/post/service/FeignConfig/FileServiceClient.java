package com.codecampus.post.service.FeignConfig;

import com.codecampus.post.dto.request.AddFileDocumentDto;
import com.codecampus.post.dto.response.AddFileResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "file-service", url = "${file.service.file-url}") // sửa tên cho phù hợp
public interface FileServiceClient {

    @PostMapping(
            value = "/file/api/FileDocument/add",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    AddFileResponseDto uploadFile(@ModelAttribute AddFileDocumentDto dto);
}
