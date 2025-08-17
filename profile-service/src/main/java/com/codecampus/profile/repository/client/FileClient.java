package com.codecampus.profile.repository.client;

import com.codecampus.profile.config.AuthenticationRequestInterceptor;
import com.codecampus.profile.config.FeignMultipartConfiguration;
import com.codecampus.profile.constant.file.FileType;
import com.codecampus.profile.dto.common.FileServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Gọi /api/FileDocument/add của file-service
 */
@FeignClient(
        name = "file-service",
        url = "${app.services.file}",
        configuration = {AuthenticationRequestInterceptor.class,
                FeignMultipartConfiguration.class})
public interface FileClient {

    @PostMapping(
            value = "/api/FileDocument/add",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    FileServiceResponse<String> uploadImage(
            @RequestPart("file") MultipartFile file,
            @RequestPart("category") FileType category,
            @RequestPart("description") String description,
            @RequestPart("tags") List<String> tags
    );
}
