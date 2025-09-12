package com.codecampus.payment.controller;

import com.codecampus.payment.dto.common.ApiResponse;
import com.codecampus.payment.dto.response.DailyDepositSummary;
import com.codecampus.payment.repository.DailyDepositSummaryProjection;
import com.codecampus.payment.service.PaymentStatisticService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment-statistics")
@RequiredArgsConstructor
public class PaymentStatisticController {
  private final PaymentStatisticService paymentStatisticService;

  @GetMapping("/daily-deposit")
  public ApiResponse<?> getDailyDepositSummary(
      @RequestParam int year, // 2004 < year < curent year
      @RequestParam int month // 1 <= month <= 12
  ) {
    return ApiResponse.builder()
        .message("Lấy thống kê thành công!")
        .result(paymentStatisticService.getDailyDepositSummaryByMonth(year, month))
        .build();
  }
}
