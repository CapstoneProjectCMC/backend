package events.org.data;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationPayload {
  String name;
  String description;
  String ownerId;
  String logoUrl;
  String email;
  String phone;
  String address;
  String status;
  Instant updatedAt;
}