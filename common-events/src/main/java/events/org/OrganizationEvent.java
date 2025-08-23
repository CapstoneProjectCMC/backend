package events.org;

import com.codecampus.constant.ScopeType;
import events.org.data.OrganizationPayload;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizationEvent {
    Type type;
    String id;             // GUID
    ScopeType scopeType;
    OrganizationPayload payload; // null nếu DELETED
    
    public enum Type { CREATED, UPDATED, DELETED, RESTORED }
}
