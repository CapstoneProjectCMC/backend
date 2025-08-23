package com.codecampus.profile.dto.request.org;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizationMemberDto {
    String id;
    String userId;
    String scopeType;  // Organization | Grade | Class
    String scopeId;
    String role;       // SuperAdmin | Admin | Teacher | Student
    boolean isActive;
    String scopeName;
}