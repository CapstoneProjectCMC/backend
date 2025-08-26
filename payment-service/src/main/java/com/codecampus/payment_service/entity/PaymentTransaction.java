package com.codecampus.payment_service.entity;

import com.codecampus.payment_service.constant.TransactionEnum;
import com.codecampus.payment_service.entity.audit.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "payment_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentTransaction extends AuditMetadata {

  @Id
  @Column(name = "transaction_id", nullable = false, unique = true)
  String transactionId;

  @Column(name = "reference_code")
  String referenceCode;

  @Column(name = "user_id", nullable = false)
  String userId;

  @Column(name = "username", nullable = false)
  String username;

  @Column(name = "type", nullable = false)
  TransactionEnum transactionType;

  @Column(name = "amount", nullable = false)
  Double amount;

  @Column(name = "currency", nullable = false)
  String currency;

  @Column(name = "status", nullable = false)
  String status = "PENDING";
}
