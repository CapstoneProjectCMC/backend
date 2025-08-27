package com.codecampus.payment_service.entity;

import com.codecampus.payment_service.entity.audit.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
    name = "purchase",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "item_id", "item_type"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Purchase extends AuditMetadata {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "purchase_id")
  String purchaseId;

  @Column(name = "user_id", nullable = false)
  String userId;

  @Column(name = "username", nullable = false)
  String username;

  @Column(name = "item_id", nullable = false)
  String itemId;

  @Column(name = "item_type", nullable = false)
  String itemType;

  @Column(name = "item_name", nullable = false)
  String itemName;

  @Column(name = "item_price", nullable = false)
  Double itemPrice;

  @Column(name = "transaction_id", nullable = false)
  String transactionId;
}
