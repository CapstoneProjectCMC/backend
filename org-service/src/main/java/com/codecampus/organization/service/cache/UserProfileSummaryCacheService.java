package com.codecampus.organization.service.cache;

import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.response.UserProfileResponse;
import com.codecampus.organization.mapper.UserMapper;
import com.codecampus.organization.repository.client.ProfileClient;
import dtos.UserProfileSummary;
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
public class UserProfileSummaryCacheService {
  Duration TTL = Duration.ofHours(6);
  String KEY_PREFIX = "userprofile:summary:";

  @Qualifier("userProfileSummaryRedisTemplate")
  RedisTemplate<String, UserProfileSummary> redis;

  ProfileClient profileClient;
  UserMapper userMapper;
  RedissonClient redisson;

  public UserProfileSummary get(String userId) {
    return redis.opsForValue().get(KEY_PREFIX + userId);
  }

  public void put(String userId, UserProfileSummary s) {
    redis.opsForValue().set(KEY_PREFIX + userId, s, TTL);
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

  public UserProfileSummary getOrLoad(String userId) {
    UserProfileSummary cached = get(userId);
    if (cached != null) {
      return cached;
    }

    RLock lock = redisson.getLock("lock:user:" + userId);
    try {
      if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {
        cached = get(userId);
        if (cached != null) {
          return cached;
        }

        ApiResponse<UserProfileResponse> api =
            profileClient.internalGetUserProfileByUserId(userId);
        UserProfileResponse profile = api != null ? api.getResult() : null;
        UserProfileSummary summary = (profile == null) ? null
            : userMapper.toUserProfileSummaryFromUserProfileResponse(profile);
        if (summary != null) {
          put(userId, summary);
        }
        return summary;
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    } catch (Exception ex) {
      log.warn("[UserCache] error {}: {}", userId, ex.getMessage());
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }

    // fallback
    try {
      ApiResponse<UserProfileResponse> api =
          profileClient.internalGetUserProfileByUserId(userId);
      return userMapper.toUserProfileSummaryFromUserProfileResponse(
          api.getResult());
    } catch (Exception ex) {
      log.warn("[UserCache] fallback error {}: {}", userId, ex.getMessage());
      return null;
    }
  }
}