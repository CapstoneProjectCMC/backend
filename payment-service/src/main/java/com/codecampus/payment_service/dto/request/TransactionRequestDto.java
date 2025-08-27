package com.codecampus.payment_service.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionRequestDto {
  private String transactionId;
  private String referenceId;
  private String transactionType; // "TOPUP", "PURCHASE"
  private double amount; // point
  private String currency; // vnd
  // for purchase
  private String itemId;
  private String itemType; // "COURSE", "SUBSCRIPTION"
  private Double itemPrice;
  private String itemName;
}
