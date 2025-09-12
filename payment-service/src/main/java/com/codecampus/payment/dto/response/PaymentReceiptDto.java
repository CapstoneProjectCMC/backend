package com.codecampus.payment.dto.response;

import com.codecampus.payment.constant.TransactionEnum;
import dtos.UserSummary;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentReceiptDto {
  String transactionId;
  TransactionEnum transactionType;
  Double amount;
  String currency;
  String status;
  Instant paidAt;
  Double balanceAfter; // số dư sau giao dịch
  UserSummary payer;
  
  // thông tin item (nếu là PURCHASE)
  String itemId;
  String itemType;
  String itemName;
}