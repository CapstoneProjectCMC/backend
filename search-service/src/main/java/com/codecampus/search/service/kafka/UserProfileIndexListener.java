package com.codecampus.search.service.kafka;

import com.codecampus.search.entity.UserProfileDocument;
import com.codecampus.search.mapper.UserProfileMapper;
import com.codecampus.search.repository.UserProfileDocumentRepository;
import com.codecampus.search.service.cache.UserSummaryCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.user.UserEvent;
import events.user.UserProfileEvent;
import events.user.UserRegisteredEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileIndexListener {
    UserProfileDocumentRepository userProfileDocumentRepository;
    ObjectMapper objectMapper;
    UserProfileMapper userProfileMapper;
    UserSummaryCacheService userSummaryCacheService;

    /* 1) Tạo doc ban đầu khi user được register (identity đã publish) */
    @KafkaListener(
            topics = "${app.event.user-registrations}", groupId = "search-service"
    )
    public void onUserRegistered(String raw) {
        try {
            UserRegisteredEvent evt =
                    objectMapper.readValue(raw, UserRegisteredEvent.class);
            UserProfileDocument doc =
                    userProfileMapper.toUserProfileDocumentFromUserRegisteredEvent(
                            evt);
            userProfileDocumentRepository.save(doc);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /* 2) Đồng bộ các thay đổi từ identity (username/email/roles/active, soft delete/restore) */
    @KafkaListener(
            topics = "${app.event.user-events}",
            groupId = "search-service"
    )
    public void onUserEvent(String raw) {
        try {
            UserEvent evt = objectMapper.readValue(raw, UserEvent.class);
            switch (evt.getType()) {
                case CREATED, UPDATED, RESTORED -> {
                    UserProfileDocument doc =
                            userProfileDocumentRepository.findById(evt.getId())
                                    .orElseGet(
                                            () -> UserProfileDocument.builder()
                                                    .userId(evt.getId())
                                                    .createdAt(Instant.now())
                                                    .build());
                    userProfileMapper.updateUserPayloadToUserProfileDocument(
                            evt.getPayload(), doc);
                    // Nếu là RESTORED => clear soft-delete
                    if (evt.getType() == UserEvent.Type.RESTORED) {
                        doc.setDeletedAt(null);
                        doc.setDeletedBy(null);
                    }
                    userProfileDocumentRepository.save(doc);
                }
                case DELETED -> {
                    userProfileDocumentRepository.findById(evt.getId())
                            .ifPresent(doc -> {
                                doc.setDeletedAt(Instant.now());
                                doc.setDeletedBy("identity-service");
                                userProfileDocumentRepository.save(doc);
                            });
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /* 3) Nhận thay đổi Profile (update avatar/background/fields, soft delete/restore) */
    @KafkaListener(topics = "${app.event.profile-events}", groupId = "search-service")
    public void onProfileEvent(String raw) {
        try {
            UserProfileEvent evt =
                    objectMapper.readValue(raw, UserProfileEvent.class);
            String userId = evt.getId();
            switch (evt.getType()) {
                case UPDATED -> {
                    var doc =
                            userProfileDocumentRepository.findById(evt.getId())
                                    .orElseGet(
                                            () -> UserProfileDocument.builder()
                                                    .userId(evt.getId())
                                                    .createdAt(Instant.now())
                                                    .build());
                    userProfileMapper.updateUserProfilePayloadToUserProfileDocument(
                            evt.getPayload(), doc);
                    userProfileDocumentRepository.save(doc);

                    // Làm mới cache ngay, tránh data stale
                    userSummaryCacheService.refresh(userId);
                }
                case DELETED ->
                        userProfileDocumentRepository.findById(evt.getId())
                                .ifPresent(doc -> {
                                    doc.setDeletedAt(Instant.now());
                                    doc.setDeletedBy("profile-service");
                                    userProfileDocumentRepository.save(doc);
                                });
                case RESTORED -> {
                    userProfileDocumentRepository.findById(evt.getId())
                            .ifPresent(doc -> {
                                doc.setDeletedAt(null);
                                doc.setDeletedBy(null);
                                userProfileDocumentRepository.save(doc);
                            });
                    
                    // User bị xoá mềm: xoá cache để tránh leak
                    userSummaryCacheService.evictTwice(userId);
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
