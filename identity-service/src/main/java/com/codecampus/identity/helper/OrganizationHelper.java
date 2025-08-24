package com.codecampus.identity.helper;

import com.codecampus.identity.dto.common.ApiResponse;
import com.codecampus.identity.dto.response.org.PrimaryOrgResponse;
import com.codecampus.identity.repository.httpclient.org.OrganizationClient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationHelper {

  OrganizationClient organizationClient;

  public PrimaryOrgResponse getPrimaryOrg(
      String userId) {
    try {
      ApiResponse<PrimaryOrgResponse> response =
          organizationClient.getPrimaryOrg(userId);
      return response != null ? response.getResult() : null;
    } catch (Exception ex) {
      log.warn("Cannot fetch primary org for user {}: {}", userId,
          ex.getMessage());
      return null;
    }
  }
}
