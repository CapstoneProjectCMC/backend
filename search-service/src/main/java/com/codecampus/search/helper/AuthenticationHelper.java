package com.codecampus.search.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class AuthenticationHelper {

  public static String getUserId() {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    if (a == null || !a.isAuthenticated()) {
      return null;
    }
    Object p = a.getPrincipal();
    if (p instanceof Jwt jwt) {
      return jwt.getClaimAsString("userId");
    }
    return null;
  }

  public static String getOrgId() {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    if (a == null || !a.isAuthenticated()) {
      return null;
    }
    Object p = a.getPrincipal();
    if (p instanceof Jwt jwt) {
      String orgId = jwt.getClaimAsString("org_id");
      return (orgId != null && !orgId.isBlank()) ? orgId : null;
    }
    return null;
  }
}