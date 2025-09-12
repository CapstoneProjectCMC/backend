package com.codecampus.organization.dto.request;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class BulkAddMembersRequest {
  List<MemberInfo> members;
  String defaultRole;       // "Admin" | "Teacher" | "Student" | "SuperAdmin"
  boolean active = true;
}
