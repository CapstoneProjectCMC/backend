package com.codecampus.payment_service.repository;

import com.codecampus.payment_service.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, String> {
  Optional<Purchase> findByUserIdAndItemIdAndItemType(String userId, String itemId, String itemType);
  Page<Purchase> findAllByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
}
