package com.codecampus.organization.dto.response;

import dtos.UserProfileSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberInBlockResponse {
  UserProfileSummary user;
  String role;
  boolean active;
}