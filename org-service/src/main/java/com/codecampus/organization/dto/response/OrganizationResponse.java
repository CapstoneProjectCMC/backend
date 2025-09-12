package com.codecampus.organization.dto.response;


import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponse {
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
}