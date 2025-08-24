package com.codecampus.identity.dto.response.authentication;

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
public class AuthenticationResponse {
  String tokenAccessType;
  String accessToken;
  String refreshToken;
  Instant accessExpiry;
  Instant refreshExpiry;
  boolean authenticated;
  boolean enabled;
}
