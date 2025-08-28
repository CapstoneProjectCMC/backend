package com.codecampus.payment.helper;

import com.codecampus.payment.dto.response.PaymentReceiptDto;
import com.codecampus.payment.dto.response.PaymentTransactionView;
import com.codecampus.payment.dto.response.PurchaseView;
import com.codecampus.payment.dto.response.WalletView;
import com.codecampus.payment.entity.PaymentTransaction;
import com.codecampus.payment.entity.Purchase;
import com.codecampus.payment.entity.Wallet;
import dtos.UserSummary;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PaymentHelper {
  public String safeCurrency(String currency) {
    return (currency == null || currency.isBlank()) ? "VND" : currency;
  }

  public PaymentReceiptDto toPaymentReceiptDto(
      PaymentTransaction tx,
      Double balanceAfter,
      Purchase purchase,
      UserSummary user) {
    return PaymentReceiptDto.builder()
        .transactionId(tx.getTransactionId())
        .transactionType(tx.getTransactionType())
        .amount(tx.getAmount())
        .currency(tx.getCurrency())
        .status(tx.getStatus())
        .paidAt(tx.getPaidAt())
        .balanceAfter(balanceAfter)
        .payer(user)
        .itemId(purchase != null ? purchase.getItemId() : null)
        .itemType(purchase != null ? purchase.getItemType() : null)
        .itemName(purchase != null ? purchase.getItemName() : null)
        .build();
  }

  public PaymentTransactionView toTransactionView(
      PaymentTransaction tx,
      UserSummary user) {
    return PaymentTransactionView.builder()
        .transactionId(tx.getTransactionId())
        .referenceCode(tx.getReferenceCode())
        .transactionType(tx.getTransactionType())
        .amount(tx.getAmount())
        .currency(tx.getCurrency())
        .status(tx.getStatus())
        .paidAt(tx.getPaidAt())
        .user(user)
        .build();
  }

  public PurchaseView toPurchaseView(Purchase p, UserSummary user) {
    return PurchaseView.builder()
        .purchaseId(p.getPurchaseId())
        .itemId(p.getItemId())
        .itemType(p.getItemType())
        .itemName(p.getItemName())
        .itemPrice(p.getItemPrice())
        .transactionId(p.getTransactionId())
        .buyer(user)
        .build();
  }

  public WalletView toWalletView(
      Wallet w, UserSummary user) {
    return WalletView.builder()
        .balance(w.getBalance())
        .owner(user)
        .build();
  }
}
