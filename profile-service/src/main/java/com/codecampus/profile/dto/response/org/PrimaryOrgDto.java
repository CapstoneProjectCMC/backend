package com.codecampus.profile.dto.response.org;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrimaryOrgDto {
  String organizationId;
  String role; // Admin | Teacher | Student | SuperAdmin
}