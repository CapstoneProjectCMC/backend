package com.codecampus.payment.repository;

import com.codecampus.payment.entity.PaymentTransaction;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository
    extends JpaRepository<PaymentTransaction, String> {
  Page<PaymentTransaction> findAllByUserIdOrderByCreatedAtDesc(
      String userId,
      Pageable pageable);

  boolean existsByReferenceCode(String referenceCode);

  Optional<PaymentTransaction> findByReferenceCode(String referenceCode);
}
