package com.codecampus.payment.service.cache;

import com.codecampus.payment.dto.common.ApiResponse;
import com.codecampus.payment.dto.response.UserProfileResponse;
import com.codecampus.payment.mapper.UserSummaryMapper;
import com.codecampus.payment.repository.client.ProfileClient;
import dtos.UserSummary;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSummaryCacheService {

  String KEY_PREFIX = "user:summary:";
  Duration TTL = Duration.ofHours(6);
  @Qualifier("userSummaryRedisTemplate")
  RedisTemplate<String, UserSummary> redis;

  ProfileClient profileClient;
  UserSummaryMapper userSummaryMapper;
  RedissonClient redisson;

  public UserSummary get(String userId) {
    return redis.opsForValue().get(KEY_PREFIX + userId);
  }

  public void put(String userId, UserSummary userSummary) {
    redis.opsForValue().set(KEY_PREFIX + userId, userSummary, TTL);
  }

  public void evict(String userId) {
    redis.delete(KEY_PREFIX + userId);
  }

  @Async
  public void evictTwice(String userId) {
    evict(userId);
    CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS)
        .execute(() -> evict(userId));
  }

  public UserSummary getOrLoad(String userId) {
    UserSummary cached = get(userId);
    if (cached != null) {
      return cached;
    }

    String lockName = "lock:user:" + userId;
    RLock lock = redisson.getLock(lockName);
    try {
      if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {
        cached = get(userId);
        if (cached != null) {
          return cached;
        }

        ApiResponse<UserProfileResponse> api =
            profileClient.internalGetUserProfileByUserId(userId);
        UserSummary summary =
            userSummaryMapper.toUserSummaryFromUserProfileResponse(
                api != null ? api.getResult() : null);
        if (summary != null) {
          put(userId, summary);
        }
        return summary;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (Exception ex) {
      log.warn("[UserCache] getOrLoad error {}: {}", userId, ex.getMessage());
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }

    // fallback no lock
    try {
      ApiResponse<UserProfileResponse> api =
          profileClient.internalGetUserProfileByUserId(userId);
      return userSummaryMapper.toUserSummaryFromUserProfileResponse(
          api.getResult());
    } catch (Exception ex) {
      log.warn("[UserCache] fallback error {}: {}", userId, ex.getMessage());
      return null;
    }
  }

  public void refresh(String userId) {
    evictTwice(userId);

    String lockName = "lock:user:" + userId;
    RLock lock = redisson.getLock(lockName);
    try {
      if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {
        ApiResponse<UserProfileResponse> api =
            profileClient.internalGetUserProfileByUserId(userId);
        UserSummary summary =
            userSummaryMapper.toUserSummaryFromUserProfileResponse(
                api.getResult());
        if (summary != null) {
          put(userId, summary);
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }
}