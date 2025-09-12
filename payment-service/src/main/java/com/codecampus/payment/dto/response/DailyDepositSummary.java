package com.codecampus.payment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DailyDepositSummary {
  private LocalDate day;
  private BigDecimal totalAmount;
}
