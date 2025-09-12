package com.codecampus.organization.service.cache;

import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.response.UserProfileResponse;
import com.codecampus.organization.mapper.UserSummaryMapper;
import com.codecampus.organization.repository.client.ProfileClient;
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

  Duration TTL = Duration.ofHours(6);
  String KEY_PREFIX = "user:summary:";

  @Qualifier("userSummaryRedisTemplate")
  RedisTemplate<String, UserSummary> redis;

  ProfileClient profileClient;
  UserSummaryMapper userSummaryMapper;
  RedissonClient redisson;

  public UserSummary get(String userId) {
    return redis.opsForValue().get(KEY_PREFIX + userId);
  }

  public void put(String userId, UserSummary summary) {
    redis.opsForValue().set(KEY_PREFIX + userId, summary, TTL);
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
        UserProfileResponse profile = api != null ? api.getResult() : null;
        UserSummary summary =
            userSummaryMapper.toUserSummaryFromUserProfileResponse(profile);
        if (summary != null) {
          put(userId, summary);
          return summary;
        }
        return null;
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

    // fallback no-lock
    try {
      var api = profileClient.internalGetUserProfileByUserId(userId);
      return userSummaryMapper.toUserSummaryFromUserProfileResponse(
          api.getResult());
    } catch (Exception ex) {
      log.warn("[UserCache] Fallback call error {}: {}", userId,
          ex.getMessage());
      return null;
    }
  }

  public void refresh(String userId) {
    evictTwice(userId);
    String lockName = "lock:user:" + userId;
    RLock lock = redisson.getLock(lockName);
    try {
      if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {
        var api = profileClient.internalGetUserProfileByUserId(userId);
        var summary = userSummaryMapper.toUserSummaryFromUserProfileResponse(
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