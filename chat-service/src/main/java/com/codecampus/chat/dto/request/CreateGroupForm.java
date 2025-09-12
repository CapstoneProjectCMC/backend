package com.codecampus.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateGroupForm {

  // participants (bao gồm các user khác; owner sẽ được service tự thêm)
  @NotNull
  @Size(min = 1)
  List<String> participantIds;

  // mặc định GROUP, vẫn cho phép override nếu cần
  @Builder.Default
  String type = "GROUP";

  // meta của group
  String name;
  String topic;

  // avatar upload (tuỳ chọn)
  MultipartFile fileAvatarGroup;
}