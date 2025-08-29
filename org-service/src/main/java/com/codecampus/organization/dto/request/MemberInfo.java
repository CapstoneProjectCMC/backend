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
public class MemberInfo {
  String userId;
  String role; // ưu tiên role của item
  Boolean active; // nếu null => dùng active ở ngoài
}
