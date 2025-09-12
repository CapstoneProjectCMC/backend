package com.codecampus.profile.service.cache;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.repository.client.PostClient;
import dtos.PostSummary;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
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
public class PostCacheService {
  private static final String KEY_PREFIX = "post:summary:";
  private static final Duration TTL = Duration.ofHours(6);

  @Qualifier("postRedisTemplate")
  private final RedisTemplate<String, PostSummary> redis;
  private final RedissonClient redisson;
  private final PostClient postClient;

  private String key(String id) {
    return KEY_PREFIX + id;
  }

  public PostSummary get(String id) {
    return redis.opsForValue().get(key(id));
  }

  public void put(String id, PostSummary dto) {
    redis.opsForValue().set(key(id), dto, TTL);
  }

  public void evict(String id) {
    redis.delete(key(id));
  }

  @Async
  public void evictTwice(String id) {
    evict(id);
    CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS)
        .execute(() -> evict(id));
  }

  public PostSummary getOrLoad(String id) {
    PostSummary cached = get(id);
    if (cached != null) {
      return cached;
    }

    String lockKey = "lock:post:" + id;
    RLock lock = redisson.getLock(lockKey);
    try {
      if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {
        cached = get(id);
        if (cached != null) {
          return cached;
        }

        ApiResponse<PostSummary> api = postClient.internalGetPostSummary(id);
        PostSummary s = api != null ? api.getResult() : null;
        if (s != null) {
          put(id, s);
        }
        return s;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      log.warn("[PostCache] getOrLoad failed {}: {}", id, e.getMessage());
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }

    try {
      ApiResponse<PostSummary> api = postClient.internalGetPostSummary(id);
      return api != null ? api.getResult() : null;
    } catch (Exception e) {
      log.warn("[PostCache] fallback no-lock failed {}: {}", id,
          e.getMessage());
      return null;
    }
  }

  public void refresh(String id) {
    evict(id);
    try {
      ApiResponse<PostSummary> api = postClient.internalGetPostSummary(id);
      if (api != null && api.getResult() != null) {
        put(id, api.getResult());
      }
    } catch (Exception e) {
      log.warn("[PostCache] refresh failed {}: {}", id, e.getMessage());
    }
  }
}