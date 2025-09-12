package com.codecampus.payment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public record DailyStatisticSummaryResponse(
    LocalDate day,
    BigDecimal depositAmount,
    BigDecimal purchaseAmount,
    BigDecimal walletBalance
) {}
