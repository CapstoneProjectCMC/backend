package com.codecampus.payment.repository;

import com.codecampus.payment.dto.response.DailyDepositSummary;
import com.codecampus.payment.entity.PaymentTransaction;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentTransactionRepository
    extends JpaRepository<PaymentTransaction, String> {
  Page<PaymentTransaction> findAllByUserIdOrderByCreatedAtDesc(
      String userId,
      Pageable pageable);

  boolean existsByReferenceCode(String referenceCode);

  Optional<PaymentTransaction> findByReferenceCode(String referenceCode);

  @Query(
      value = """
        SELECT 
            CAST(paid_at AS DATE) AS day,
            SUM(amount) AS totalAmount
        FROM payment_transaction
        WHERE paid_at BETWEEN :startDate AND :endDate
          AND status = 'SUCCESS'
          AND type = 0
        GROUP BY CAST(paid_at AS DATE)
        ORDER BY CAST(paid_at AS DATE)
        """,
      nativeQuery = true
  )
  List<DailyDepositSummaryProjection> getDailyDepositSummary(Instant startDate, Instant endDate);


}
