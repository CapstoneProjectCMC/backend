package com.codecampus.payment.entity;

import com.codecampus.payment.constant.TransactionEnum;
import com.codecampus.payment.entity.audit.AuditMetadata;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

  @Column(name = "currency")
  String currency;

  @Column(name = "status", nullable = false)
  @Builder.Default
  String status = "PENDING";

  @Column(name = "paid_at", nullable = false)
  Instant paidAt;
}
