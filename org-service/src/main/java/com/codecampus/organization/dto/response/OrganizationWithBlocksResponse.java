package com.codecampus.organization.dto.response;


import com.codecampus.organization.dto.common.PageResponse;
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
public class OrganizationWithBlocksResponse {
  String id;
  String name;
  String description;
  String logoUrl;
  String email;
  String phone;
  String address;
  String status;
  Instant createdAt;
  Instant updatedAt;

  PageResponse<BlockResponse> blocks;
}