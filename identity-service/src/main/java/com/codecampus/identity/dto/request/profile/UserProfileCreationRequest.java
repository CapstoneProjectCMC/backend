package com.codecampus.identity.dto.request.profile;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileCreationRequest
{
  String userId;
  String firstName;
  String lastName;
  Instant dob;
  String avatarUrl;
  String backgroundUrl;
  String bio;
  boolean gender;
  String displayName;
  int education;
  String[] links;
  String city;
}
