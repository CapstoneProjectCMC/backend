package com.codecampus.identity.dto.response.org;


import lombok.Data;

@Data
public class MemberInBlockLite {
  String userId;
  String role;
  boolean active;
}
