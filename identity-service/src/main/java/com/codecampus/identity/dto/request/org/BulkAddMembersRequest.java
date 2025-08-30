package com.codecampus.identity.dto.request.org;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkAddMembersRequest {
  @Builder.Default
  boolean active = true;
  String defaultRole;
  List<MemberInfo> members;
}
