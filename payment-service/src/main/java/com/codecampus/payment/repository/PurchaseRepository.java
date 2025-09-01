package com.codecampus.payment.repository;

import com.codecampus.payment.entity.Purchase;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository
    extends JpaRepository<Purchase, String> {
  Optional<Purchase> findByUserIdAndItemIdAndItemType(
      String userId,
      String itemId,
      String itemType);

  Page<Purchase> findAllByUserIdOrderByCreatedAtDesc(
      String userId,
      Pageable pageable);

  boolean existsByUserIdAndItemIdAndItemType(
      String userId, String itemId,
      String itemType);

  List<Purchase> findByUserIdAndItemIdInAndItemType(
      String userId, Collection<String> itemIds, String itemType);
}
