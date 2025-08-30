package com.codecampus.organization.dto.response;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockWithMembersResponse {
  String id;         // với block ảo “Unassigned” có thể để null hoặc "virtual"
  String orgId;
  String name;
  String code;
  String description;
  Instant createdAt;
  Instant updatedAt;
  List<MemberInBlockResponse> members;
}