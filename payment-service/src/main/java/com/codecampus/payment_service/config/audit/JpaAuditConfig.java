package com.codecampus.payment_service.config.audit;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditConfig {
  /**
   * Trả về username hiện tại (hoặc “system” nếu chưa đăng nhập).
   * Spring Data sẽ dùng giá trị này để gán vào @CreatedBy / @LastModifiedBy.
   */
  @Bean
  public AuditorAware<String> auditorProvider() {
    return () -> {
      Authentication auth = SecurityContextHolder
          .getContext()
          .getAuthentication();

      return Optional.ofNullable(auth)
          .map(Authentication::getName)
          .or(() -> Optional.of("system"));
    };
  }
}
