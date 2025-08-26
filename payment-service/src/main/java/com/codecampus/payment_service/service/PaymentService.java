package com.codecampus.payment_service.service;

import com.codecampus.payment_service.config.CustomJwtDecoder;
import com.codecampus.payment_service.constant.TransactionEnum;
import com.codecampus.payment_service.dto.common.PageResponse;
import com.codecampus.payment_service.dto.request.TransactionRequestDto;
import com.codecampus.payment_service.entity.PaymentTransaction;
import com.codecampus.payment_service.entity.Purchase;
import com.codecampus.payment_service.entity.Wallet;
import com.codecampus.payment_service.repository.PaymentTransactionRepository;
import com.codecampus.payment_service.repository.PurchaseRepository;
import com.codecampus.payment_service.repository.WalletRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final WalletRepository walletRepository;
  private final PaymentTransactionRepository transactionRepository;
  private final PurchaseRepository purchaseRepository;
  private final CustomJwtDecoder jwtDecoder;

  /** Nạp tiền */
  @Transactional
  public void topUp(TransactionRequestDto request, HttpServletRequest httpRequest) {
    String token = httpRequest.getHeader("Authorization");
    String username = jwtDecoder.decode(token.substring(7))
        .getClaims()
        .get("username")
        .toString();
    String userId = jwtDecoder.decode(token.substring(7))
        .getClaims()
        .get("userId")
        .toString();

    Wallet wallet = walletRepository.findByUserId(userId)
        .orElseGet(() -> walletRepository.save(Wallet.builder()
            .userId(userId)
            .balance(0.0)
            .build()));

    // tạo transaction
    PaymentTransaction tx = PaymentTransaction.builder()
        .transactionId(request.getTransactionId())
        .userId(userId)
        .username(username)
        .transactionType(TransactionEnum.TOPUP)
        .currency(request.getCurrency())
        .amount(request.getAmount())
        .status("SUCCESS")
        .referenceCode(request.getReferenceId())
        .build();

    transactionRepository.save(tx);

    // cộng tiền
    wallet.setBalance(wallet.getBalance() + request.getAmount());
    walletRepository.save(wallet);

//    return tx;
  }

  /** Mua item */
  @Transactional
  public void purchase(TransactionRequestDto request, HttpServletRequest httpRequest) {
    String token = httpRequest.getHeader("Authorization");
    if (token == null || !token.startsWith("Bearer ")) {
      throw new RuntimeException("Missing or invalid Authorization header");
    }
    String username = jwtDecoder.decode(token.substring(7))
        .getClaims()
        .get("username")
        .toString();
    String userId = jwtDecoder.decode(token.substring(7))
        .getClaims()
        .get("userId")
        .toString();

    Wallet wallet = walletRepository.findByUserId(userId)
        .orElseThrow(() -> new RuntimeException("Wallet not found"));

    if (wallet.getBalance() < request.getItemPrice()) {
      throw new RuntimeException("Insufficient balance");
    }

    // check đã mua chưa
    Optional<Purchase> existed = purchaseRepository.findByUserIdAndItemIdAndItemType(userId,
        request.getItemId(),
        request.getItemType());
    if (existed.isPresent()) {
      throw new RuntimeException("Item already purchased");
    }

    // tạo transaction
    PaymentTransaction tx = PaymentTransaction.builder()
        .transactionId(request.getTransactionId())
        .userId(userId)
        .transactionType(TransactionEnum.PURCHASE)
        .amount(request.getItemPrice())
        .status("SUCCESS")
        .referenceCode(request.getReferenceId())
        .build();
    transactionRepository.save(tx);

    // trừ tiền
    wallet.setBalance(wallet.getBalance() - request.getItemPrice());
    walletRepository.save(wallet);

    // lưu purchase
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

//    return tx;
  }

  @Transactional(readOnly = true)
  public PageResponse<PaymentTransaction> getTransactionHistory(int page, int size, HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token == null || !token.startsWith("Bearer ")) {
      throw new RuntimeException("Missing or invalid Authorization header");
    }

    String userId = jwtDecoder.decode(token.substring(7))
        .getClaims()
        .get("userId")
        .toString();

    Pageable pageable = PageRequest.of(page, size);
    Page<PaymentTransaction> transactions = transactionRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);
    return PageResponse.<PaymentTransaction>builder()
        .data(transactions.getContent())
        .currentPage(transactions.getNumber())
        .totalElements(transactions.getTotalElements())
        .totalPages(transactions.getTotalPages())
        .pageSize(transactions.getSize())
        .build();
  }

  @Transactional(readOnly = true)
  public PageResponse<Purchase> getPurchaseHistory(int page, int size, HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token == null || !token.startsWith("Bearer ")) {
      throw new RuntimeException("Missing or invalid Authorization header");
    }

    String userId = jwtDecoder.decode(token.substring(7))
        .getClaims()
        .get("userId")
        .toString();

    PageRequest pageable = PageRequest.of(page, size);
    Page<Purchase> purchases = purchaseRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);

    return PageResponse.<Purchase>builder()
        .currentPage(purchases.getNumber() + 1)
        .pageSize(purchases.getSize())
        .totalPages(purchases.getTotalPages())
        .totalElements(purchases.getTotalElements())
        .data(purchases.getContent())
        .build();
  }
}
