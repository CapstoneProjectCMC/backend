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
public class CreateOrganizationMemberRequest {
  String userId;
  String scopeType = "Organization"; // mặc định
  String scopeId;
  String role;       // "Admin" | "Teacher" | "Student" | "SuperAdmin"
  boolean isActive = true;
}
