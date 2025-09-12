package com.codecampus.payment.repository;

import com.codecampus.payment.entity.PaymentTransaction;
import com.codecampus.payment.repository.projection.DailyDepositSummaryProjection;
import com.codecampus.payment.repository.projection.DailyStatisticSummaryProjection;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

  @Query(value = """
      WITH opening_balance AS (
          SELECT COALESCE(SUM(CASE WHEN pt.type = 0 THEN pt.amount ELSE -pt.amount END), 0) AS balance
          FROM payment_transaction pt
          WHERE pt.user_id = :userId
            AND pt.status = 'SUCCESS'
            AND pt.paid_at < :startDate
      )
      SELECT
          d.day,
          d.depositAmount,
          d.purchaseAmount,
          ob.balance + SUM(d.depositAmount - d.purchaseAmount)
              OVER (ORDER BY d.day ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS walletBalance
      FROM (
          SELECT
              CAST(pt.paid_at AS DATE) AS day,
              SUM(CASE WHEN pt.type = 0 THEN pt.amount ELSE 0 END) AS depositAmount,
              SUM(CASE WHEN pt.type = 1 THEN pt.amount ELSE 0 END) AS purchaseAmount
          FROM payment_transaction pt
          WHERE pt.user_id = :userId
            AND pt.status = 'SUCCESS'
            AND pt.paid_at BETWEEN :startDate AND :endDate
          GROUP BY CAST(pt.paid_at AS DATE)
      ) d
      CROSS JOIN opening_balance ob
      ORDER BY d.day;               
        """, nativeQuery = true)
  List<DailyStatisticSummaryProjection> getDailyDepositSummaryByUser(
      String userId,
      Instant startDate,
      Instant endDate);
}
