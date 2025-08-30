package com.codecampus.organization.dto.request;

import com.codecampus.organization.dto.common.PageResponse;
import com.codecampus.organization.dto.response.MemberInBlockResponse;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockWithMembersResponse {
  String id;
  String orgId;
  String name;
  String code;
  String description;
  Instant createdAt;
  Instant updatedAt;

  // Phân trang thành viên trong block
  PageResponse<MemberInBlockResponse> members;
}