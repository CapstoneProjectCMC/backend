package com.codecampus.identity.dto.request.profile;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserProfileCreationRequest {
  String userId;
  String firstName;
  String lastName;
  Instant dob;
  String bio;
  boolean gender;
  String displayName;
  int education;
  String[] links;
  String city;
}
