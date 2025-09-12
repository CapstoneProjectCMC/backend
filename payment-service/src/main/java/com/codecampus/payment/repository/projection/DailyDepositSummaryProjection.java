package com.codecampus.payment.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailyDepositSummaryProjection {
  LocalDate getDay();
  BigDecimal getTotalAmount();
}

