package com.codecampus.payment.controller;

import com.codecampus.payment.dto.common.ApiResponse;
import com.codecampus.payment.dto.common.PageResponse;
import com.codecampus.payment.dto.request.TransactionRequestDto;
import com.codecampus.payment.dto.response.PaymentReceiptDto;
import com.codecampus.payment.dto.response.PaymentTransactionView;
import com.codecampus.payment.dto.response.PurchaseView;
import com.codecampus.payment.dto.response.WalletView;
import com.codecampus.payment.service.PaymentService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

  PaymentService paymentService;

  /**
   * Nạp tiền
   */
  @PostMapping("/topup")
  ApiResponse<PaymentReceiptDto> topUp(
      @RequestBody TransactionRequestDto request) {
    return ApiResponse.<PaymentReceiptDto>builder()
        .message("Nạp tiền thành công!")
        .result(paymentService.topUp(request))
        .build();
  }

  /**
   * Mua item
   */
  @PostMapping("/purchase")
  ApiResponse<PaymentReceiptDto> purchase(
      @RequestBody TransactionRequestDto request) {
    return ApiResponse.<PaymentReceiptDto>builder()
        .message("Mua hàng thành công!")
        .result(paymentService.purchase(request))
        .build();
  }

  /**
   * Lịch sử giao dịch
   */
  @GetMapping("/history")
  ApiResponse<PageResponse<PaymentTransactionView>> getTransactionHistory(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<PaymentTransactionView>>builder()
        .message("Get lịch sử giao dịch thành công!")
        .result(paymentService.getTransactionHistory(page, size))
        .build();
  }

  /**
   * Lịch sử mua hàng
   */
  @GetMapping("/purchase-history")
  ApiResponse<PageResponse<PurchaseView>> getPurchaseHistory(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<PurchaseView>>builder()
        .message("Get lịch sử mua hàng thành công!")
        .result(paymentService.getPurchaseHistory(page, size))
        .build();
  }

  @GetMapping("/wallet")
  ApiResponse<WalletView> getWallet() {
    return ApiResponse.<WalletView>builder()
        .result(paymentService.getBalance())
        .message("Lấy ví thành công!")
        .build();
  }
}
