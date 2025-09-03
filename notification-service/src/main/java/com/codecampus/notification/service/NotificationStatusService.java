package com.codecampus.notification.service;

import com.codecampus.notification.entity.NotificationDocument;
import com.codecampus.notification.repository.NotificationRepository;
import com.mongodb.client.result.UpdateResult;
import java.time.Instant;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationStatusService {

  MongoTemplate mongoTemplate;
  NotificationRepository notificationRepository;
  SocketPushService socketPushService;
  NotificationRealtimeService realtimeService;

  public long markRead(
      String recipient,
      Set<String> ids, Instant at) {
    if (ids == null || ids.isEmpty()) {
      return 0;
    }

    Instant ts = at != null ? at : Instant.now();
    Query q = new Query(Criteria.where("recipient").is(recipient)
        .and("id").in(ids));
    Update u = new Update()
        .set("readStatus", "READ")
        .set("readAt", ts);
    UpdateResult r =
        mongoTemplate.updateMulti(q, u, NotificationDocument.class);

    // push realtime để UI cập nhật
    socketPushService.pushToUserEvent(recipient, "notification-status",
        java.util.Map.of(
            "action", "READ",
            "ids", ids,
            "at", ts
        )
    );

    realtimeService.pushUnreadBadge(recipient);

    log.info("[Notification] markRead recipient={} ids={} modified={}",
        recipient, ids, r.getModifiedCount());
    return r.getModifiedCount();
  }

  public long markUnread(String recipient, Set<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return 0;
    }

    Query q = new Query(Criteria.where("recipient").is(recipient)
        .and("id").in(ids));
    Update u = new Update()
        .set("readStatus", "UNREAD")
        .unset("readAt");
    UpdateResult r =
        mongoTemplate.updateMulti(q, u, NotificationDocument.class);

    socketPushService.pushToUserEvent(recipient, "notification-status",
        java.util.Map.of(
            "action", "UNREAD",
            "ids", ids
        )
    );

    realtimeService.pushUnreadBadge(recipient);

    log.info("[Notification] markUnread recipient={} ids={} modified={}",
        recipient, ids, r.getModifiedCount());
    return r.getModifiedCount();
  }

  public long markAllRead(String recipient, Instant before, Instant at) {
    Instant ts = at != null ? at : Instant.now();
    Criteria c = Criteria.where("recipient").is(recipient)
        .and("readStatus").ne("READ");
    if (before != null) {
      c = c.and("createdAt").lte(before);
    }
    Query q = new Query(c);
    Update u = new Update()
        .set("readStatus", "READ")
        .set("readAt", ts);
    UpdateResult r =
        mongoTemplate.updateMulti(q, u, NotificationDocument.class);

    socketPushService.pushToUserEvent(recipient, "notification-status",
        java.util.Map.of(
            "action", "READ_ALL",
            "before", before,
            "at", ts
        )
    );

    realtimeService.pushUnreadBadge(recipient);

    log.info("[Notification] markAllRead recipient={} before={} modified={}",
        recipient, before, r.getModifiedCount());
    return r.getModifiedCount();
  }
}