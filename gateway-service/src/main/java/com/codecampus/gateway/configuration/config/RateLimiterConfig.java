package com.codecampus.gateway.configuration.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Cấu hình bộ giới hạn tốc độ (Rate Limiter) cho Gateway.
 * Bao gồm:
 * <ul>
 *   <li>Khởi tạo Cache để lưu trữ các Bucket giới hạn tốc độ theo key.</li>
 *   <li>Phương thức tạo Bucket mới dựa trên cấu hình capacity và refill.</li>
 * </ul>
 */
@Configuration
public class RateLimiterConfig {
    /**
     * Tạo và cấu hình Cache sử dụng Caffeine để lưu các Bucket và
     * tự động loại bỏ các mục không truy cập sau 1 giờ, giới hạn tối đa 10.000 mục.
     *
     * @return Cache với key là String và giá trị là Bucket
     */
    @Bean
    public Cache<String, Bucket> bucketCache() {
        return Caffeine.newBuilder()
                .expireAfterAccess(1, TimeUnit.HOURS)
                .maximumSize(10_000)
                .build();
    }

    /**
     * Tạo một Bucket mới với giới hạn capacity và cơ chế refill định kỳ.
     *
     * @param capacity       Số token tối đa trong Bucket (năng lực lưu trữ)
     * @param refillTokens   Số token được thêm vào Bucket trong mỗi chu kỳ refill
     * @param refillDuration Khoảng thời gian giữa các chu kỳ refill
     * @return Bucket đã cấu hình giới hạn và refill
     */
    public Bucket createNewBucket(
            int capacity,
            int refillTokens,
            Duration refillDuration) {
        Refill refill = Refill.intervally(refillTokens, refillDuration);
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        return Bucket.builder().addLimit(limit).build();
    }
}
