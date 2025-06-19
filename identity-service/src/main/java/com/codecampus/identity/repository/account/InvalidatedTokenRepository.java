package com.codecampus.identity.repository.account;

import com.codecampus.identity.entity.account.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository
    extends JpaRepository<InvalidatedToken, String> {
}
