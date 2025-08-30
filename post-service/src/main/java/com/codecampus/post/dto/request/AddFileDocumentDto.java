package com.codecampus.post.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddFileDocumentDto {
  private MultipartFile file;
  private String category;
  private String description;
  private List<String> tags;
  private boolean isLectureVideo = false;
  private boolean isTextbook = false;
  private String orgId;
}
