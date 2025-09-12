package com.codecampus.organization.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdNameResponse {
  String id;
  String name;
  String code;
  String orgId;
}