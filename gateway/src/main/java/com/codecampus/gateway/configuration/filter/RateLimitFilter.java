package com.codecampus.gateway.configuration.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class RateLimitFilter implements Filter
{
  private final Bucket bucket;

  public RateLimitFilter()
  {
    // Giới hạn 100 requests/phút cho mỗi IP
    Bandwidth limit = Bandwidth.classic(
        100,
        Refill.intervally(100, Duration.ofMinutes(1))
    );

    this.bucket = Bucket4j.builder()
        .addLimit(limit)
        .build();
  }

  @Override
  public void doFilter(
      ServletRequest servletRequest,
      ServletResponse servletResponse,
      FilterChain filterChain)
      throws IOException, ServletException
  {
    if (bucket.tryConsume(1))
    {
      filterChain.doFilter(servletRequest, servletResponse);
    } else
    {
      ((HttpServletResponse) servletResponse).setStatus(429);
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException
  {
    // Initialization code if needed
  }

  @Override
  public void destroy()
  {
    // Cleanup code if needed
  }
}
