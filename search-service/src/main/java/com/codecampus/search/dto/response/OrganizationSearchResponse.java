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
public class OrganizationSearchResponse {
  String id;
  String name;
  String description;
  String ownerId;
  String logoUrl;
  String email;
  String phone;
  String address;
  String status;
  Instant createdAt;
  Instant updatedAt;

  // hydrate từ org-service khi cần
  PageResponse<BlockWithMembersWithUserPageResponse> blocks;
}