package com.codecampus.quiz.service.cache;

import com.codecampus.quiz.grpc.LoadQuizResponse;
import com.codecampus.quiz.mapper.QuizMapper;
import com.codecampus.quiz.repository.QuizExerciseRepository;
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

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoadQuizCacheService {

    Duration TTL = Duration.ofHours(1);
    String KEY_PREFIX = "quiz:"; // quiz:<exerciseId>

    @Qualifier("loadQuizRedisTemplate")
    RedisTemplate<String, LoadQuizResponse> redisTemplate;

    QuizExerciseRepository quizExerciseRepository;

    QuizMapper quizMapper;

    RedissonClient redisson;


    public LoadQuizResponse get(String exerciseId) {
        return redisTemplate.opsForValue()
                .get(KEY_PREFIX + exerciseId);
    }

    public void put(
            String exerciseId,
            LoadQuizResponse loadQuizResponse) {

        redisTemplate
                .opsForValue()
                .set(KEY_PREFIX + exerciseId,
                        loadQuizResponse,
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

        String lockName = "lock:quiz:" + exerciseId;
        RLock lock = redisson.getLock(lockName);

        try {
            if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {
                // Lấy snapshot mới nhất từ DB
                quizExerciseRepository.findById(exerciseId).ifPresent(quiz -> {
                    LoadQuizResponse loadQuizResponse =
                            quizMapper.toLoadQuizResponseFromQuizExercise(quiz);
                    put(exerciseId, loadQuizResponse); // ghi lại cache
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
