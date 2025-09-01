package com.codecampus.search.dto.response;

import java.time.Instant;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
  String userId;

  String username;
  String email;
  Boolean active;
  Set<String> roles;

  String firstName;
  String lastName;
  String dob; // string dd/MM/yyyy (UTC)
  String bio;
  Boolean gender;
  String displayName;
  Integer education;
  String[] links;
  String city;
  String avatarUrl;
  String backgroundUrl;
  Instant createdAt;
}