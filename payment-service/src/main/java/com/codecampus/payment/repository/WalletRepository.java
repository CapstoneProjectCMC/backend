package com.codecampus.payment.repository;

import com.codecampus.payment.entity.Wallet;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface WalletRepository
    extends JpaRepository<Wallet, String> {
  Optional<Wallet> findByUserId(
      String userId);

  /**
   * Dùng trong giao dịch để khoá hàng ví, tránh race khi cộng/trừ tiền
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select w from Wallet w where w.userId = :userId")
  Optional<Wallet> findByUserIdForUpdate(String userId);
}
