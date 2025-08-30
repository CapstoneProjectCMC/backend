package com.codecampus.organization.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class SwitchBlockRequest {
  String fromBlockId;
  String toBlockId;
  /**
   * Optional: nếu truyền sẽ override role ở block mới (mặc định giữ role cũ hoặc Student)
   */
  String role;
}
