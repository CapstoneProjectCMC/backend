package com.codecampus.payment_service.controller;
import com.codecampus.payment_service.dto.common.ApiResponse;
import com.codecampus.payment_service.dto.common.PageResponse;
import com.codecampus.payment_service.dto.request.TransactionRequestDto;
import com.codecampus.payment_service.entity.PaymentTransaction;
import com.codecampus.payment_service.entity.Purchase;
import com.codecampus.payment_service.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  /** Nạp tiền */
  @PostMapping("/topup")
  public ApiResponse<PaymentTransaction> topUp(@RequestBody TransactionRequestDto request,
                                               HttpServletRequest httpRequest) {
    paymentService.topUp(request, httpRequest);
    return ApiResponse.<PaymentTransaction>builder()
        .message("Nạp tiền thành công")
        .build();
  }

  /** Mua item */
  @PostMapping("/purchase")
  public ApiResponse<?> purchase(@RequestBody TransactionRequestDto request, HttpServletRequest httpRequest) {
    paymentService.purchase(request, httpRequest);
    return ApiResponse.builder()
        .message("Mua hàng thành công")
        .build();
  }

  /** Lịch sử giao dịch */
  @GetMapping("/history")
  public PageResponse<PaymentTransaction> getTransactionHistory(@RequestParam int page,
                                                              @RequestParam int size,
                                                              HttpServletRequest request) {
  return paymentService.getTransactionHistory(page, size, request);
  }

  /** Lịch sử mua hàng */
  @GetMapping("/purchase-history")
  public PageResponse<Purchase> getPurchaseHistory(@RequestParam int page,
                                                   @RequestParam int size,
                                                   HttpServletRequest request) {
  return paymentService.getPurchaseHistory(page, size, request);
  }

  @GetMapping("/wallet")
    public ApiResponse<?> getWallet(HttpServletRequest request) {
        return ApiResponse.builder()
            .result(paymentService.getBalance(request))
            .message("Lấy ví thành công")
            .build();
    }
}
