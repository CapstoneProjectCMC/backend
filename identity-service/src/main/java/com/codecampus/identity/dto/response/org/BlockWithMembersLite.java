package com.codecampus.identity.dto.response.org;

import com.codecampus.identity.dto.common.PageResponse;
import java.time.Instant;
import lombok.Data;

@Data
public class BlockWithMembersLite {
  String id;
  String orgId;
  String name;
  String code;
  String description;
  Instant createdAt;
  Instant updatedAt;
  PageResponse<MemberInBlockLite> members;
}