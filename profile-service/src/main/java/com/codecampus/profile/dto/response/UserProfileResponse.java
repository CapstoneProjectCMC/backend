package com.codecampus.profile.dto.response;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// Mặc định cho phép response cả null khi Dev
// Khi build thì KHÔNG response null
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {
  String id;
  String userId;
  String firstName;
  String lastName;
  String dob;
  String bio;
  boolean gender;
  String displayName;
  int education;
  String[] links;
  String city;
  String avatarUrl;
  String backgroundUrl;
  Instant createdAt;
}
