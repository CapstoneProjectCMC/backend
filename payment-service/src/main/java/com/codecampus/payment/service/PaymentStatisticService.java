package com.codecampus.payment.service;

import com.codecampus.payment.dto.response.DailyDepositSummary;
import com.codecampus.payment.exception.AppException;
import com.codecampus.payment.exception.ErrorCode;
import com.codecampus.payment.helper.AuthenticationHelper;
import com.codecampus.payment.repository.DailyDepositSummaryProjection;
import com.codecampus.payment.repository.PaymentTransactionRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentStatisticService {
  private final PaymentTransactionRepository paymentTransactionRepository;

  public List<DailyDepositSummaryProjection> getDailyDepositSummaryByMonth(int year, int month) {
    if (!AuthenticationHelper.getMyRoles().contains("ADMIN")) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }
    if (year < 2004 || year > Year.now().getValue() ) {
      throw new AppException(ErrorCode.YEAR_INVALID);
    }
    if (month < 1 || month > 12) {
      throw new AppException(ErrorCode.MONTH_INVALID);
    }


    YearMonth ym = YearMonth.of(year, month);
    LocalDate start = ym.atDay(1);
    LocalDate end = ym.atEndOfMonth();

    Instant startDate = start.atStartOfDay().toInstant(ZoneOffset.UTC);
    Instant endDate = end.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

    return paymentTransactionRepository.getDailyDepositSummary(startDate, endDate);
  }
}
