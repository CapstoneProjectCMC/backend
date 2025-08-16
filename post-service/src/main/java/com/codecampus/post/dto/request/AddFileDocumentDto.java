package com.codecampus.post.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
public class AddFileDocumentDto {
    private MultipartFile file;
    private String category; // enum truyền dạng string
    private String description;
    private List<String> tags;
    private boolean isLectureVideo;
    private boolean isTextbook;
    private UUID orgId;
}
