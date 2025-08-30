package com.codecampus.identity.dto.request.org;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfo {
  String userId;
  String role;
  Boolean active;
}
