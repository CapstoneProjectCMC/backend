// submission-service
package com.codecampus.submission.service.cache;

import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.response.profile.UserProfileResponse;
import com.codecampus.submission.mapper.UserSummaryMapper;
import com.codecampus.submission.repository.client.ProfileClient;
import dtos.UserSummary;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSummaryCacheService {

    Duration TTL = Duration.ofHours(6);
    String KEY_PREFIX = "user:summary:"; // user:summary:<userId>

    @Qualifier("userSummaryRedisTemplate")
    RedisTemplate<String, UserSummary> redis;

    ProfileClient profileClient;
    UserSummaryMapper userSummaryMapper;

    RedissonClient redisson;

    public UserSummary get(String userId) {
        return redis.opsForValue().get(KEY_PREFIX + userId);
    }

    public Map<String, UserSummary> mapGet(Collection<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }

        List<String> keys = userIds.stream()
                .map(id -> KEY_PREFIX + id).toList();
        List<UserSummary> vals = redis.opsForValue().multiGet(keys);
        Map<String, UserSummary> out = new HashMap<>();
        int i = 0;
        for (String id : userIds) {
            UserSummary userSummary =
                    (vals != null && i < Objects.requireNonNull(keys).size()) ?
                            vals.get(i) : null;
            if (userSummary != null) {
                out.put(id, userSummary);
            }
            i++;
        }
        return out;

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

        // Xoá lần 2 sau 0.1 giây khi mọi node đã flush write-ahead log
        CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS)
                .execute(() -> evict(userId));
    }

    /**
     * Read-through: Lấy từ cache, nếu miss thì lock, gọi profile-service, rồi ghi cache.
     * Đảm bảo không "tắc" khi nhiều request đồng thời đòi user giống nhau.
     */
    public UserSummary getOrLoad(String userId) {

        // 1) thử cache
        UserSummary cached = get(userId);
        if (cached != null) {
            return cached;
        }

        // 2) stampede lock theo user
        String lockName = "lock:user:" + userId;
        RLock lock = redisson.getLock(lockName);

        try {
            if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {
                // double-check sau khi có lock
                cached = get(userId);
                if (cached != null) {
                    return cached;
                }

                // gọi profile-service
                ApiResponse<UserProfileResponse> api =
                        profileClient.internalGetUserProfileByUserId(userId);
                UserProfileResponse profile = (api != null) ? api.getResult() :
                        null;
                UserSummary summary =
                        userSummaryMapper.toUserSummaryFromUserProfileResponse(
                                profile);
                if (summary != null) {
                    put(userId, summary);
                    return summary;
                }
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            log.warn("[UserCache] getOrLoad lỗi {}: {}", userId,
                    ex.getMessage());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        // 3) fallback: gọi thẳng (không lock được)
        try {
            ApiResponse<UserProfileResponse> api =
                    profileClient.internalGetUserProfileByUserId(userId);
            return userSummaryMapper.toUserSummaryFromUserProfileResponse(
                    api.getResult());
        } catch (Exception ex) {
            log.warn("[UserCache] Fallback call profile-service lỗi {}: {}",
                    userId, ex.getMessage());
            return null;
        }
    }

    /**
     * Cho phép refresh chủ động (khi có event Kafka)
     */
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
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
