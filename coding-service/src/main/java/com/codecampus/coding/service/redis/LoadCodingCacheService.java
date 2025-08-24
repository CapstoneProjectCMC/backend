package com.codecampus.coding.service.redis;

import com.codecampus.coding.grpc.LoadCodingResponse;
import com.codecampus.coding.mapper.CodingMapper;
import com.codecampus.coding.repository.CodingExerciseRepository;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LoadCodingCacheService {

  Duration TTL = Duration.ofHours(1);
  String KEY_PREFIX = "coding:";

  @Qualifier("loadCodingRedisTemplate")
  RedisTemplate<String, LoadCodingResponse> redisTemplate;

  CodingExerciseRepository codingExerciseRepository;

  CodingMapper codingMapper;

  RedissonClient redisson;

  public LoadCodingResponse get(String exerciseId) {
    return redisTemplate.opsForValue()
        .get(KEY_PREFIX + exerciseId);
  }

  public void put(
      String exerciseId,
      LoadCodingResponse loadCodingResponse) {

    redisTemplate
        .opsForValue()
        .set(KEY_PREFIX + exerciseId,
            loadCodingResponse,
            TTL);
  }

  public void evict(String exerciseId) {
    redisTemplate.delete(KEY_PREFIX + exerciseId);
  }

  /**
   * Double-delete để hạn chế cache-miss do race-condition
   */
  @Async
  public void evictTwice(String exerciseId) {
    evict(exerciseId); // Xoá ngay

    // Xoá lần 2 sau 0.1 giây khi mọi node đã flush write-ahead log
    CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS)
        .execute(() -> evict(exerciseId));
  }

  /**
   * Xoá cache, ghi lại snapshot mới (dùng lock để tránh stampede).
   * Gọi method này ngay sau khi bạn đã save/update DB thành công.
   */
  public void refresh(String exerciseId) {

    evictTwice(exerciseId);          // lần xoá thứ 1 ngay lập tức

    String lockName = "lock:coding:" + exerciseId;
    RLock lock = redisson.getLock(lockName);

    try {
      if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {
        // Lấy snapshot mới nhất từ DB
        codingExerciseRepository.findById(exerciseId)
            .ifPresent(coding -> {
              LoadCodingResponse loadCodingResponse =
                  codingMapper.toLoadCodingResponseFromCodingExercise(
                      coding);
              put(exerciseId,
                  loadCodingResponse); // ghi lại cache
            });
      }
    } catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }
}
