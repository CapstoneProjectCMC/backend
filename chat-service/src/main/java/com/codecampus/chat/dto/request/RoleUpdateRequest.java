package com.codecampus.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleUpdateRequest {
  String userId; // người được set role
  String role; // OWNER/ADMIN/MEMBER
}