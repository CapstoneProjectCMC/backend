package com.codecampus.notification.service;

import com.codecampus.notification.dto.common.PageResponse;
import com.codecampus.notification.dto.response.NotificationView;
import com.codecampus.notification.entity.NotificationDocument;
import com.codecampus.notification.helper.NotificationHelper;
import com.codecampus.notification.helper.PageResponseHelper;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
  MongoTemplate mongoTemplate;
  NotificationHelper notificationHelper;

  public PageResponse<NotificationView> getMyNotifications(
      String recipient,
      int page,
      int size,
      String readStatus,          // null/ALL/READ/UNREAD
      Instant from,
      Instant to
  ) {
    int p = Math.max(1, page - 1);
    int s = (size < 1 || size > 200) ? 20 : size;

    Criteria c = Criteria.where("recipient").is(recipient);
    if (readStatus != null && !readStatus.equalsIgnoreCase("ALL")) {
      c = c.and("readStatus").is(readStatus.toUpperCase());
    }
    if (from != null && to != null) {
      c = c.and("createdAt").gte(from).lte(to);
    } else if (from != null) {
      c = c.and("createdAt").gte(from);
    } else if (to != null) {
      c = c.and("createdAt").lte(to);
    }

    Query q = new Query(c);
    Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
    Pageable pageable = PageRequest.of(p - 1, s, sort);
    q.with(pageable);

    List<NotificationDocument> docs =
        mongoTemplate.find(q, NotificationDocument.class);

    long total = mongoTemplate.count(
        Query.of(q).limit(-1).skip(-1), NotificationDocument.class);

    List<NotificationView> content = docs.stream()
        .map(notificationHelper::toNotificationVewFromNotificationDocument)
        .collect(Collectors.toList());

    Page<NotificationView> springPage =
        new PageImpl<>(content, pageable, total);

    return PageResponseHelper.toPageResponse(springPage, p);
  }

  public NotificationView getNotification(String recipient, String id) {
    Query q = new Query(Criteria.where("id").is(id)
        .and("recipient").is(recipient));
    NotificationDocument doc =
        mongoTemplate.findOne(q, NotificationDocument.class);
    if (doc == null) {
      return null;
    }
    return notificationHelper.toNotificationVewFromNotificationDocument(doc);
  }

  public long countMyUnread(String recipient) {
    Query q = new Query(Criteria.where("recipient").is(recipient)
        .and("readStatus").is("UNREAD"));
    return mongoTemplate.count(q, NotificationDocument.class);
  }
}
