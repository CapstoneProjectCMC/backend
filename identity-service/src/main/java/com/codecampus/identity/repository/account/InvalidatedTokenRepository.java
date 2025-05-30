package com.codecampus.identity.repository.account;

import com.codecampus.identity.entity.account.InvalidatedToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository
    extends JpaRepository<InvalidatedToken, String>
{
}
