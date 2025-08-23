package com.codecampus.submission.service.cache;

import dtos.UserSummary;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserBulkLoader {

    UserSummaryCacheService userSummaryCacheService;

    // Hạn chế concurrency để không dồn tải profile-service
    // Số luồng tối thiểu là 4
    // Chỉ dùng 1 nửa số core, tránh chếm hết tài nguyên CPU
    ExecutorService pool = Executors.newFixedThreadPool(Math.max(4,
            Runtime.getRuntime().availableProcessors() / 2));

    public Map<String, UserSummary> loadAll(Set<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 1) Lấy từ cache trước
        Map<String, UserSummary> result = new HashMap<>();
        List<String> misses = new ArrayList<>();

        for (String userId : userIds) {
            UserSummary u = userSummaryCacheService.get(userId);
            if (u != null) {
                result.put(userId, u);
            } else {
                misses.add(userId);
            }

        }
        if (misses.isEmpty()) {
            return result;
        }

        // 2) Gọi song song nhưng giới hạn thread
        // Callable<T> là một interface giống như Runnable
        // Runnable.run() không trả về kết quả, chỉ thực hiện logic.
        // Callable.call() có thể trả về kết quả và có thể ném exception.
        List<Callable<Void>> tasks = misses.stream()
                .map(uid -> (Callable<Void>) () -> {
                    UserSummary s = userSummaryCacheService.getOrLoad(uid);
                    if (s != null) {
                        synchronized (result) {
                            result.put(uid, s);
                        }
                    }
                    return null;
                }).toList();

        // future.get() → chặn cho đến khi task hoàn thành và trả về kết quả
        // (hoặc ném ExecutionException nếu task fail).
        try {
            // submit tất cả các Callable cùng lúc,
            // giới hạn số thread tối đa theo pool.
            List<Future<Void>> futures = pool.invokeAll(tasks);
            for (Future<Void> f : futures) {
                f.get(); // join
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException ignored) {
        }

        return result;
    }

}
