package events.payment;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisePurchasedEvent {
  String transactionId;
  String referenceCode;
  String userId;
  String username;

  String exerciseId;
  Double price;
  String currency;

  Instant paidAt;
}