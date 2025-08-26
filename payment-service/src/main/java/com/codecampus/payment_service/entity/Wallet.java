package com.codecampus.payment_service.entity;

import com.codecampus.payment_service.entity.audit.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wallet extends AuditMetadata {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "wallet_id")
  String walletId;

  @Column(name = "user_id", nullable = false, unique = true)
  String userId;

  @Column(name = "username", nullable = false)
  String username;

  @Column(name = "balance", nullable = false)
  Double balance = 0.0;
}
