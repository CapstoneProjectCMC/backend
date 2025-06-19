package com.codecampus.submission.configuration.audit;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {
  @Override
  public Optional<String> getCurrentAuditor() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return Optional.empty();
    }

    // Token kiểu JwtAuthenticationToken
    // (spring-security-oauth2-resource-server)
    if (auth instanceof JwtAuthenticationToken jwtAuth) {
      Jwt jwt = jwtAuth.getToken();

      // JWT của bạn có claim "username"
      return Optional.ofNullable(jwt.getClaimAsString("username"));
    }

    // Fallback – lấy principal name
    return Optional.ofNullable(auth.getName());
  }
}
