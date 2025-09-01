package com.codecampus.payment.service;

import com.codecampus.payment.constant.TransactionEnum;
import com.codecampus.payment.dto.common.PageResponse;
import com.codecampus.payment.dto.request.TransactionRequestDto;
import com.codecampus.payment.dto.response.PaymentReceiptDto;
import com.codecampus.payment.dto.response.PaymentTransactionView;
import com.codecampus.payment.dto.response.PurchaseView;
import com.codecampus.payment.dto.response.WalletView;
import com.codecampus.payment.entity.PaymentTransaction;
import com.codecampus.payment.entity.Purchase;
import com.codecampus.payment.entity.Wallet;
import com.codecampus.payment.exception.AppException;
import com.codecampus.payment.exception.ErrorCode;
import com.codecampus.payment.helper.AuthenticationHelper;
import com.codecampus.payment.helper.PageResponseHelper;
import com.codecampus.payment.helper.PaymentHelper;
import com.codecampus.payment.repository.PaymentTransactionRepository;
import com.codecampus.payment.repository.PurchaseRepository;
import com.codecampus.payment.repository.WalletRepository;
import com.codecampus.payment.service.cache.UserBulkLoader;
import com.codecampus.payment.service.cache.UserSummaryCacheService;
import dtos.UserSummary;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {

  WalletRepository walletRepository;
  PaymentTransactionRepository transactionRepository;
  PurchaseRepository purchaseRepository;
  PaymentHelper paymentHelper;

  UserSummaryCacheService userSummaryCacheService;
  UserBulkLoader userBulkLoader;

  /**
   * Nạp tiền
   */
  @Transactional
  public PaymentReceiptDto topUp(
      TransactionRequestDto request) {

    String userId = AuthenticationHelper.getMyUserId();
    if (userId == null) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }
    String username = AuthenticationHelper.getMyUsername();

    if (request.getAmount() <= 0) {
      throw new AppException(
          ErrorCode.INVALID_REQUEST);
    }
    if (request.getTransactionId() == null ||
        request.getTransactionId().isBlank()) {
      throw new AppException(ErrorCode.INVALID_REQUEST);
    }

    if (transactionRepository.existsById(request.getTransactionId())) {
      throw new AppException(ErrorCode.DUPLICATE_TRANSACTION_ID);
    }

    if (!paymentHelper.isBlank(request.getReferenceId())
        &&
        transactionRepository.existsByReferenceCode(request.getReferenceId())) {
      throw new AppException(ErrorCode.DUPLICATE_REFERENCE_CODE);
    }

    // lock ví (tạo nếu chưa có)
    Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
        .orElseGet(() -> walletRepository.save(Wallet.builder()
            .userId(userId).username(username).balance(0.0).build()));

    PaymentTransaction tx = PaymentTransaction.builder()
        .transactionId(request.getTransactionId())
        .referenceCode(request.getReferenceId())
        .userId(userId)
        .username(username)
        .transactionType(TransactionEnum.TOPUP)
        .currency(paymentHelper.safeCurrency(request.getCurrency()))
        .amount(request.getAmount())
        .status("SUCCESS")
        .paidAt(Instant.now()) // <= set paidAt
        .build();
    transactionRepository.save(tx);

    wallet.setBalance(wallet.getBalance() + request.getAmount());
    walletRepository.save(wallet);

    UserSummary us = userSummaryCacheService.getOrLoad(userId);
    return paymentHelper.toPaymentReceiptDto(
        tx, wallet.getBalance(),
        null, us);
  }

  @Transactional
  public PaymentReceiptDto purchase(
      TransactionRequestDto request) {
    String userId = AuthenticationHelper.getMyUserId();
    if (userId == null) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }
    String username = AuthenticationHelper.getMyUsername();

    if (request.getItemPrice() == null || request.getItemPrice() <= 0
        || request.getItemId() == null || request.getItemType() == null
        || request.getTransactionId() == null ||
        request.getTransactionId().isBlank()) {
      throw new AppException(ErrorCode.INVALID_REQUEST);
    }

    if (transactionRepository.existsById(request.getTransactionId())) {
      throw new AppException(ErrorCode.DUPLICATE_TRANSACTION_ID);
    }

    if (!paymentHelper.isBlank(request.getReferenceId())
        &&
        transactionRepository.existsByReferenceCode(request.getReferenceId())) {
      throw new AppException(ErrorCode.DUPLICATE_REFERENCE_CODE);
    }

    Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
        .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

    if (wallet.getBalance() < request.getItemPrice()) {
      throw new AppException(ErrorCode.INSUFFICIENT_BALANCE);
    }

    purchaseRepository.findByUserIdAndItemIdAndItemType(userId,
            request.getItemId(), request.getItemType())
        .ifPresent(p -> {
          throw new AppException(ErrorCode.PURCHASED_ITEM);
        });

    PaymentTransaction tx = PaymentTransaction.builder()
        .transactionId(request.getTransactionId())
        .referenceCode(request.getReferenceId())
        .userId(userId)
        .username(username)
        .transactionType(TransactionEnum.PURCHASE)
        .amount(request.getItemPrice())
        .currency(paymentHelper.safeCurrency(request.getCurrency()))
        .status("SUCCESS")
        .paidAt(Instant.now())                    // <= set paidAt
        .build();
    transactionRepository.save(tx);

    wallet.setBalance(wallet.getBalance() - request.getItemPrice());
    walletRepository.save(wallet);

    Purchase purchase = Purchase.builder()
        .userId(userId)
        .username(username)
        .itemId(request.getItemId())
        .itemType(request.getItemType())
        .itemName(request.getItemName())
        .itemPrice(request.getItemPrice())
        .transactionId(tx.getTransactionId())
        .build();
    purchaseRepository.save(purchase);

    UserSummary us = userSummaryCacheService.getOrLoad(userId);
    return paymentHelper.toPaymentReceiptDto(tx, wallet.getBalance(), purchase,
        us);
  }

  @Transactional(readOnly = true)
  public PageResponse<PaymentTransactionView> getTransactionHistory(
      int page, int size) {
    String userId = AuthenticationHelper.getMyUserId();
    Pageable pageable = PageRequest.of(Math.max(page, 1) - 1, size);
    Page<PaymentTransaction> p = transactionRepository
        .findAllByUserIdOrderByCreatedAtDesc(userId, pageable);

    // enrich user summaries
    Set<String> uids =
        p.getContent().stream().map(PaymentTransaction::getUserId)
            .collect(Collectors.toSet());
    Map<String, UserSummary> summaries = userBulkLoader.loadAll(uids);

    Page<PaymentTransactionView> mapped =
        p.map(tx -> paymentHelper.toTransactionView(tx,
            summaries.get(tx.getUserId())));
    return PageResponseHelper.toPageResponse(mapped, page);
  }

  @Transactional(readOnly = true)
  public PageResponse<PurchaseView> getPurchaseHistory(
      int page, int size) {
    String userId = AuthenticationHelper.getMyUserId();
    PageRequest pageable = PageRequest.of(Math.max(page, 1) - 1, size);
    Page<Purchase> p =
        purchaseRepository.findAllByUserIdOrderByCreatedAtDesc(userId,
            pageable);

    Set<String> uids = p.getContent().stream().map(Purchase::getUserId)
        .collect(Collectors.toSet());
    Map<String, UserSummary> summaries = userBulkLoader.loadAll(uids);

    Page<PurchaseView> mapped = p.map(
        pc -> paymentHelper.toPurchaseView(pc, summaries.get(pc.getUserId())));
    return PageResponseHelper.toPageResponse(mapped, page);
  }

  @Transactional(readOnly = true)
  public WalletView getBalance() {
    String userId = AuthenticationHelper.getMyUserId();
    Wallet w = walletRepository.findByUserId(userId)
        .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));
    UserSummary us = userSummaryCacheService.getOrLoad(userId);
    return paymentHelper.toWalletView(w, us);
  }
}
