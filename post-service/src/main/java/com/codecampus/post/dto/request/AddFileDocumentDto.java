package com.codecampus.post.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddFileDocumentDto {
    private MultipartFile file;
    private String category; // enum truyền dạng string
    private String description;
    private List<String> tags;
    private boolean isLectureVideo = false;
    private boolean isTextbook = false;
    private String orgId;
}
