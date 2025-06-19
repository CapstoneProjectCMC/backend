package com.codecampus.profile.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileUpdateRequest {
  String firstName;
  String lastName;
  String dob;
  String bio;
  boolean gender;
  String displayName;
  int education;
  String[] links;
  String city;
}
