package events.org;

import com.codecampus.constant.ScopeType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationMemberEvent {
  Type type;
  String userId;         // GUID
  ScopeType scopeType;
  String scopeId;        // GUID
  String role;           // SuperAdmin|Admin|Teacher|Student
  boolean isActive;
  Instant at;

  public enum Type { ADDED, UPDATED, DELETED }
}