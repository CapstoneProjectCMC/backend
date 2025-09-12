package com.codecampus.identity.dto.response.org;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlocksOfUserResponse {
  String userId;
  List<String> blockIds;
}