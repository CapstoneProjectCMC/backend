package com.codecampus.organization.helper;

import java.util.Locale;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrganizationMemberHelper {
  public String normalizeRole(String role) {
    if (role == null) {
      return "Student";
    }
    return switch (role.toLowerCase(Locale.ROOT)) {
      case "superadmin" -> "SuperAdmin";
      case "admin" -> "Admin";
      case "teacher" -> "Teacher";
      case "student" -> "Student";
      default -> role;
    };
  }
}
