package com.codecampus.payment.service.cache;

import dtos.UserSummary;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBulkLoader {

  private final UserSummaryCacheService userSummaryCacheService;

  ExecutorService pool = Executors.newFixedThreadPool(Math.max(4,
      Runtime.getRuntime().availableProcessors() / 2));

  public Map<String, UserSummary> loadAll(Set<String> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<String, UserSummary> result = new ConcurrentHashMap<>();
    List<String> misses = new CopyOnWriteArrayList<>();

    userIds.forEach(uid -> {
      UserSummary u = userSummaryCacheService.get(uid);
      if (u != null) {
        result.put(uid, u);
      } else {
        misses.add(uid);
      }
    });

    if (misses.isEmpty()) {
      return result;
    }

    List<Callable<Void>> tasks = misses.stream()
        .map(uid -> (Callable<Void>) () -> {
          UserSummary s = userSummaryCacheService.getOrLoad(uid);
          if (s != null) {
            result.put(uid, s);
          }
          return null;
        }).toList();

    try {
      List<Future<Void>> fs = pool.invokeAll(tasks);
      for (Future<Void> f : fs) {
        f.get();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (ExecutionException ignored) {
    }

    return result;
  }
}