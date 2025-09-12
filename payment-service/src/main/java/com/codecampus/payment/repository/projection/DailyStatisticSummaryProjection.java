package com.codecampus.payment.repository.projection;

import com.codecampus.payment.constant.TransactionEnum;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailyStatisticSummaryProjection {
  LocalDate getDay();
  BigDecimal getDepositAmount();
  BigDecimal getPurchaseAmount();
  BigDecimal getWalletBalance();
}
