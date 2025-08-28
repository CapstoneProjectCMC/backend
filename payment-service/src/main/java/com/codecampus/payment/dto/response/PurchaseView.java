package com.codecampus.payment.dto.response;

import dtos.UserSummary;
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
public class PurchaseView {
  String purchaseId;
  String itemId;
  String itemType;
  String itemName;
  Double itemPrice;
  String transactionId;
  UserSummary buyer;
}