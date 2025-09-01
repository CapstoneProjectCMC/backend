package com.codecampus.search.dto.response;

import com.codecampus.search.dto.common.PageResponse;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlockWithMembersPageResponse {
  String id;
  String orgId;
  String name;
  String code;
  String description;
  Instant createdAt;
  Instant updatedAt;
  PageResponse<MemberInBlockResponse> members;
}