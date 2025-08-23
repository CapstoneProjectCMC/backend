package events.org.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationPayload {
    String name;
    String description;
    String logoUrl;
    String email;
    String phone;
    String address;
    String status;
    Instant updatedAt;
}