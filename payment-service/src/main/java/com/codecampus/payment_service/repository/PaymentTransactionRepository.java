package com.codecampus.payment_service.repository;

import com.codecampus.payment_service.entity.PaymentTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {
  Page<PaymentTransaction> findAllByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
}
