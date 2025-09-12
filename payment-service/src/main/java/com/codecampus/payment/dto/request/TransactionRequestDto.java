package com.codecampus.payment.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionRequestDto {
  String transactionId;
  String referenceId;
  double amount; // point
  String currency; // vnd
  // for purchase
  String itemId;
  String itemType; // "COURSE", "SUBSCRIPTION"
  Double itemPrice;
  String itemName;
}
