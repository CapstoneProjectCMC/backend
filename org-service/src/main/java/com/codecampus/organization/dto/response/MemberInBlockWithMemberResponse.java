package com.codecampus.organization.dto.response;

import dtos.UserSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberInBlockWithMemberResponse {
  UserSummary user;
  String role;
  boolean active;
}
