package com.codecampus.notification.repository;

import com.codecampus.notification.entity.NotificationDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository
    extends MongoRepository<NotificationDocument, String> {
  List<NotificationDocument> findByRecipientOrderByCreatedAtDesc(
      String recipient);
}