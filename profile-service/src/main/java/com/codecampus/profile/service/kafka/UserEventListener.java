package com.codecampus.profile.service.kafka;

import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.repository.UserProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.user.UserEvent;
import events.user.data.UserPayload;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserEventListener {

    UserProfileRepository userProfileRepository;
    ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.event.user-events}",
            groupId = "profile-service"
    )
    public void onMessageUser(String raw) {
        try {
            UserEvent userEvent = objectMapper.readValue(
                    raw,
                    UserEvent.class);

            switch (userEvent.getType()) {
                case CREATED, UPDATED, RESTORED ->
                        upsert(userEvent.getPayload());
                case DELETED -> softDelete(userEvent.getId());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    void upsert(UserPayload userPayload) {
        Optional<UserProfile> userProfile =
                userProfileRepository.findByUserId(
                        userPayload.getUserId());

        UserProfile profile = userProfile.orElseGet(() -> UserProfile.builder()
                .userId(userPayload.getUserId())
                .createdAt(Instant.now())
                .build());

        profile.setUsername(userPayload.getUsername());
        profile.setEmail(userPayload.getEmail());
        profile.setActive(userPayload.isActive());
        if (profile.getDeletedAt() != null && userPayload.isActive()) {
            // nếu muốn auto-restore khi nhận RESTORED/UPDATED active=true
            profile.setDeletedAt(null);
            profile.setDeletedBy(null);
        }
        userProfileRepository.save(profile);
    }

    private void softDelete(String userId) {
        userProfileRepository.findByUserId(userId).ifPresent(p -> {
            if (p.getDeletedAt() == null) {
                p.setDeletedAt(Instant.now());
                p.setDeletedBy("identity-service");
                userProfileRepository.save(p);
            }
        });
    }
}
