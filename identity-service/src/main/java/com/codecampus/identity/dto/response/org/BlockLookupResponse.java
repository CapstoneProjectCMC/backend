package com.codecampus.identity.dto.response.org;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockLookupResponse {
  String id;
  String orgId;
  String name;
  String code;
}