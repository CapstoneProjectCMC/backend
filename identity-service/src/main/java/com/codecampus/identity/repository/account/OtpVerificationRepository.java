package com.codecampus.identity.repository.account;

import com.codecampus.identity.entity.account.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository
        extends JpaRepository<OtpVerification, String> {
    Optional<OtpVerification> findByEmail(String email);
}
