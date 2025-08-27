package com.codecampus.identity.dto.request.org;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrganizationMemberRequest {
  String userId;
  String scopeType = "Organization"; // cố định "Organization"
  String scopeId; // orgId
  String role; // "ADMIN" | "TEACHER" | "STUDENT"
  boolean isActive = true;
}