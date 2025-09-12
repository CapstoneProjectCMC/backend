package com.codecampus.payment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyDepositSummaryResponse (
    LocalDate day,
    BigDecimal totalAmount
) {}
