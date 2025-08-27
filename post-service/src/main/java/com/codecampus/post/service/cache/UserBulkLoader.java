package com.codecampus.post.service.cache;

import dtos.UserSummary;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserBulkLoader {
  UserSummaryCacheService userSummaryCacheService;

  ExecutorService pool = Executors.newFixedThreadPool(
      Math.max(4, Runtime.getRuntime().availableProcessors() / 2));

  public Map<String, UserSummary> loadAll(Set<String> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return Collections.emptyMap();
    }

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

    try {
      List<Future<Void>> futures = pool.invokeAll(tasks);
      for (Future<Void> f : futures) {
        f.get();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (ExecutionException ignored) {
    }
    return result;
  }
}
