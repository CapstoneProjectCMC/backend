package com.codecampus.profile.service.cache;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.repository.client.SubmissionClient;
import dtos.ContestStatusDto;
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
public class ContestStatusCacheService {

  String KEY_PREFIX = "contest:status:"; // contest:status:<userId>:<contestId>
  Duration TTL = Duration.ofHours(3);

  @Qualifier("contestStatusRedisTemplate")
  RedisTemplate<String, ContestStatusDto> redis;

  RedissonClient redisson;
  SubmissionClient submissionClient;

  String key(String userId, String contestId) {
    return KEY_PREFIX + userId + ":" + contestId;
  }

  public ContestStatusDto get(String userId, String contestId) {
    return redis.opsForValue().get(key(userId, contestId));
  }

  public void put(String userId, String contestId, ContestStatusDto dto) {
    redis.opsForValue().set(key(userId, contestId), dto, TTL);
  }

  public void evict(String userId, String contestId) {
    redis.delete(key(userId, contestId));
  }

  @Async
  public void evictTwice(String userId, String contestId) {
    evict(userId, contestId);
    CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS)
        .execute(() -> evict(userId, contestId));
  }

  public ContestStatusDto getOrLoad(String userId, String contestId) {
    var cached = get(userId, contestId);
    if (cached != null) {
      return cached;
    }

    String lockKey = "lock:contest:status:" + userId + ":" + contestId;
    RLock lock = redisson.getLock(lockKey);
    try {
      if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {
        cached = get(userId, contestId);
        if (cached != null) {
          return cached;
        }

        ApiResponse<ContestStatusDto> api =
            submissionClient.internalGetContestStatus(contestId, userId);
        if (api != null && api.getResult() != null) {
          put(userId, contestId, api.getResult());
          return api.getResult();
        }
        return null;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    } catch (Exception e) {
      log.warn("[ContestStatusCache] fallback failed {}/{}: {}", userId,
          contestId, e.getMessage());
      return null;
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }

    // fallback no-lock
    try {
      ApiResponse<ContestStatusDto> api =
          submissionClient.internalGetContestStatus(contestId, userId);
      return api != null ? api.getResult() : null;
    } catch (Exception e) {
      log.warn("[ContestStatusCache] fallback (no-lock) failed {}/{}: {}",
          userId, contestId, e.getMessage());
      return null;
    }
  }

  public void refresh(String userId, String contestId) {
    evict(userId, contestId);
    try {
      ApiResponse<ContestStatusDto> api =
          submissionClient.internalGetContestStatus(contestId, userId);
      if (api != null && api.getResult() != null) {
        put(userId, contestId, api.getResult());
      }
    } catch (Exception e) {
      log.warn("[ContestStatusCache] refresh failed {}/{}: {}", userId,
          contestId, e.getMessage());
    }
  }
}