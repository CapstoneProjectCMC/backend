package com.codecampus.payment.controller;

import com.codecampus.payment.dto.common.ApiResponse;
import com.codecampus.payment.service.PaymentService;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/internal")
public class InternalPaymentController {

  PaymentService paymentService;

  @GetMapping("/purchase/check/{userId}")
  public ApiResponse<Boolean> internalHasPurchased(
      @PathVariable String userId,
      @RequestParam String itemId,
      @RequestParam String itemType
  ) {
    return ApiResponse.<Boolean>builder()
        .result(paymentService.hasPurchased(userId, itemId, itemType))
        .message("Check purchase theo userId thành công")
        .build();
  }

  @PostMapping("/purchase/check/bulk/{userId}")
  public ApiResponse<Map<String, Boolean>> internalHasPurchasedBulk(
      @PathVariable String userId,
      @RequestParam String itemType,
      @RequestBody List<String> itemIds
  ) {
    return ApiResponse.<Map<String, Boolean>>builder()
        .result(paymentService
            .hasPurchasedBulk(userId, itemIds, itemType))
        .message("Bulk check purchase thành công")
        .build();
  }
}
