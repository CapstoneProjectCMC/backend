package com.codecampus.identity.repository.account;

import com.codecampus.identity.entity.account.OtpVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpVerificationRepository
    extends JpaRepository<OtpVerification, String> {
  Optional<OtpVerification> findByEmail(String email);
}
