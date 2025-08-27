package com.codecampus.profile.service.cache;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.repository.client.SubmissionClient;
import dtos.ContestSummary;
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
public class ContestCacheService {

  String KEY_PREFIX = "contest:summary:";
  Duration TTL = Duration.ofHours(6);

  @Qualifier("contestRedisTemplate")
  RedisTemplate<String, ContestSummary> redis;

  RedissonClient redisson;
  SubmissionClient submissionClient;

  public ContestSummary get(String id) {
    return redis.opsForValue().get(KEY_PREFIX + id);
  }

  public void put(String id, ContestSummary dto) {
    redis.opsForValue().set(KEY_PREFIX + id, dto, TTL);
  }

  public void evict(String id) {
    redis.delete(KEY_PREFIX + id);
  }

  @Async
  public void evictTwice(String id) {
    evict(id);
    CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS)
        .execute(() -> evict(id));
  }

  public ContestSummary getOrLoad(String id) {
    var cached = get(id);
    if (cached != null) {
      return cached;
    }

    String lockKey = "lock:contest:" + id;
    RLock lock = redisson.getLock(lockKey);
    try {
      if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {
        cached = get(id);
        if (cached != null) {
          return cached;
        }

        ApiResponse<ContestSummary> api =
            submissionClient.internalGetContestSummary(id);
        if (api != null && api.getResult() != null) {
          put(id, api.getResult());
          return api.getResult();
        }
        return null;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }

    try {
      ApiResponse<ContestSummary> api =
          submissionClient.internalGetContestSummary(id);
      return api != null ? api.getResult() : null;
    } catch (Exception e) {
      log.warn("[ContestCache] fallback (no-lock) failed for {}: {}", id,
          e.getMessage());
      return null;
    }
  }

  public void refresh(String id) {
    evict(id);
    try {
      ApiResponse<ContestSummary> api =
          submissionClient.internalGetContestSummary(id);
      if (api != null && api.getResult() != null) {
        put(id, api.getResult());
      }
    } catch (Exception e) {
      log.warn("[ContestCache] refresh failed {}: {}", id, e.getMessage());
    }
  }
}
