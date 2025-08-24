package com.codecampus.identity.dto.response.org;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrimaryOrgResponse {
  String organizationId; // GUID dạng string
  String role;           // "Admin", "Teacher", "Student", ...
}
