package com.codecampus.post.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IntrospectResponse {
  boolean valid;
  String userId;
}