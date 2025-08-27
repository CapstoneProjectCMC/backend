package com.codecampus.profile.service.cache;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.repository.client.SubmissionClient;
import dtos.ExerciseSummary;
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
public class ExerciseCacheService {
  String KEY_PREFIX = "exercise:summary:";
  Duration TTL = Duration.ofHours(6);

  @Qualifier("exerciseRedisTemplate")
  RedisTemplate<String, ExerciseSummary> redis;
  RedissonClient redisson;
  SubmissionClient submissionClient;

  public ExerciseSummary get(String id) {
    return redis.opsForValue().get(KEY_PREFIX + id);
  }

  public void put(String id, ExerciseSummary dto) {
    redis.opsForValue().set(KEY_PREFIX + id, dto, TTL);
  }

  public void evict(String id) {
    redis.delete(KEY_PREFIX + id);
  }

  @Async
  public void evictTwice(String exerciseId) {
    evict(exerciseId);

    // Xoá lần 2 sau 0.1 giây khi mọi node đã flush write-ahead log
    CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS)
        .execute(() -> evict(exerciseId));
  }

  public ExerciseSummary getOrLoad(String id) {
    var cached = get(id);
    if (cached != null) {
      return cached;
    }

    String lockKey = "lock:exercise:" + id;
    RLock lock = redisson.getLock(lockKey);
    try {
      if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {
        cached = get(id);
        if (cached != null) {
          return cached;
        }

        ApiResponse<ExerciseSummary> api =
            submissionClient.internalGetExerciseSummary(id);
        if (api != null && api.getResult() != null) {
          put(id, api.getResult());
          return api.getResult();
        }
        return null;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    } catch (Exception e) {
      log.warn("[ExerciseCache] fallback call failed for {}: {}", id,
          e.getMessage());
      return null;
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }

    // fallback không lock được
    try {
      ApiResponse<ExerciseSummary> api =
          submissionClient.internalGetExerciseSummary(id);
      return api != null ? api.getResult() : null;
    } catch (Exception e) {
      log.warn("[ExerciseCache] fallback (no-lock) failed for {}: {}", id,
          e.getMessage());
      return null;
    }
  }

  public void refresh(String id) {
    evict(id);
    try {
      ApiResponse<ExerciseSummary> api =
          submissionClient.internalGetExerciseSummary(id);
      if (api != null && api.getResult() != null) {
        put(id, api.getResult());
      }
    } catch (Exception e) {
      log.warn("[ExerciseCache] refresh failed {}: {}", id, e.getMessage());
    }
  }
}
