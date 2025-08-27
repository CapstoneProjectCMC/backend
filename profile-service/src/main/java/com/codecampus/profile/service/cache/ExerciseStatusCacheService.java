package com.codecampus.profile.service.cache;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.repository.client.SubmissionClient;
import dtos.ExerciseStatusDto;
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
public class ExerciseStatusCacheService {

  String KEY_PREFIX = "exercise:status:";
  // exercise:status:<userId>:<exerciseId>
  Duration TTL = Duration.ofHours(3);

  @Qualifier("exerciseStatusRedisTemplate")
  RedisTemplate<String, ExerciseStatusDto> redis;

  RedissonClient redisson;
  SubmissionClient submissionClient;

  String key(String userId, String exerciseId) {
    return KEY_PREFIX + userId + ":" + exerciseId;
  }

  public ExerciseStatusDto get(String userId, String exerciseId) {
    return redis.opsForValue().get(key(userId, exerciseId));
  }

  public void put(String userId, String exerciseId, ExerciseStatusDto dto) {
    redis.opsForValue().set(key(userId, exerciseId), dto, TTL);
  }

  public void evict(String userId, String exerciseId) {
    redis.delete(key(userId, exerciseId));
  }

  @Async
  public void evictTwice(String userId, String exerciseId) {
    evict(userId, exerciseId);
    CompletableFuture
        .delayedExecutor(100, TimeUnit.MILLISECONDS)
        .execute(() -> evict(userId, exerciseId));
  }

  public ExerciseStatusDto getOrLoad(String userId, String exerciseId) {
    var cached = get(userId, exerciseId);
    if (cached != null) {
      return cached;
    }

    String lockKey = "lock:exercise:status:" + userId + ":" + exerciseId;
    RLock lock = redisson.getLock(lockKey);
    try {
      if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {
        cached = get(userId, exerciseId);
        if (cached != null) {
          return cached;
        }

        ApiResponse<ExerciseStatusDto> api =
            submissionClient.internalGetExerciseStatus(exerciseId, userId);
        if (api != null && api.getResult() != null) {
          put(userId, exerciseId, api.getResult());
          return api.getResult();
        }
        return null;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    } catch (Exception e) {
      log.warn("[ExerciseStatusCache] fallback failed {}/{}: {}", userId,
          exerciseId, e.getMessage());
      return null;
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }

    // fallback no-lock
    try {
      ApiResponse<ExerciseStatusDto> api =
          submissionClient.internalGetExerciseStatus(exerciseId, userId);
      return api != null ? api.getResult() : null;
    } catch (Exception e) {
      log.warn("[ExerciseStatusCache] fallback (no-lock) failed {}/{}: {}",
          userId, exerciseId, e.getMessage());
      return null;
    }
  }

  public void refresh(String userId, String exerciseId) {
    evict(userId, exerciseId);
    try {
      ApiResponse<ExerciseStatusDto> api =
          submissionClient.internalGetExerciseStatus(exerciseId, userId);
      if (api != null && api.getResult() != null) {
        put(userId, exerciseId, api.getResult());
      }
    } catch (Exception e) {
      log.warn("[ExerciseStatusCache] refresh failed {}/{}: {}", userId,
          exerciseId, e.getMessage());
    }
  }
}