package com.codecampus.identity.dto.request.authentication;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class UserCreationRequest
{
  String username;
  String email;
  String password;

  // Profile
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
