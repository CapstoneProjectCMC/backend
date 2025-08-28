package com.codecampus.payment.entity;

import com.codecampus.payment.entity.audit.AuditMetadata;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
