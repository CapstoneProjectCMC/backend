package com.codecampus.organization.dto.response;

import dtos.UserSummary;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlocksOfUserWithMemberResponse {
  UserSummary user;   // <== thay vì String userId
  List<String> blockIds;
}
