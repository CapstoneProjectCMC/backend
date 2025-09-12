package com.codecampus.organization.helper;

import com.codecampus.organization.entity.OrganizationMember;
import com.codecampus.organization.service.kafka.OrganizationMemberEventProducer;
import events.org.OrganizationMemberEvent;
import java.time.Instant;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationMemberHelper {

  OrganizationMemberEventProducer eventProducer;

  public String normalizeRole(String role) {
    if (role == null) {
      return "STUDENT";
    }
    return switch (role.toUpperCase(Locale.ROOT)) {
      case "ADMIN" -> "ADMIN";
      case "TEACHER" -> "TEACHER";
      case "STUDENT" -> "STUDENT";
      default -> role;
    };
  }

  public void publishEvent(
      OrganizationMember member,
      OrganizationMemberEvent.Type type) {
    eventProducer.publish(OrganizationMemberEvent.builder()
        .type(type)
        .userId(member.getUserId())
        .scopeType(member.getScopeType())
        .scopeId(member.getScopeId())
        .role(member.getRole())
        .isActive(member.isActive())
        .at(Instant.now())
        .build());
  }

  public String getString(Row r, int idx) {
    if (r.getCell(idx) == null) {
      return "";
    }
    r.getCell(idx).setCellType(CellType.STRING);
    String s = r.getCell(idx).getStringCellValue();
    return s == null ? "" : s.trim();
  }

  public Boolean parseBoolean(String raw, boolean dft) {
    if (raw == null || raw.isBlank()) {
      return dft;
    }
    return "true".equalsIgnoreCase(raw) || "1".equals(raw);
  }
}
