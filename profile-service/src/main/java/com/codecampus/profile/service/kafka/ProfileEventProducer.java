package com.codecampus.profile.service.kafka;

import com.codecampus.profile.entity.UserProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.user.UserProfileEvent;
import events.user.data.UserProfilePayload;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileEventProducer {

    KafkaTemplate<String, String> kafkaTemplate;
    ObjectMapper objectMapper;

    @Value("${app.event.profile-events}")
    @NonFinal
    String PROFILE_EVENTS_TOPIC;

    public void publishUpdated(UserProfile profile) {
        send(eventOf(UserProfileEvent.Type.UPDATED, profile));
    }

    public void publishDeleted(UserProfile profile) {
        send(eventOf(UserProfileEvent.Type.DELETED, profile));
    }

    public void publishRestored(UserProfile profile) {
        send(eventOf(UserProfileEvent.Type.RESTORED, profile));
    }

    private UserProfileEvent eventOf(
            UserProfileEvent.Type type,
            UserProfile p) {
        return UserProfileEvent.builder()
                .type(type)
                .id(p.getUserId())
                .payload(type == UserProfileEvent.Type.DELETED ? null :
                        toPayload(p))
                .build();
    }

    private UserProfilePayload toPayload(UserProfile p) {
        return UserProfilePayload.builder()
                .userId(p.getUserId())
                .username(p.getUsername())
                .email(p.getEmail())
                .active(Boolean.TRUE.equals(p.getActive()))
                .roles(p.getRoles())

                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .dob(p.getDob())
                .bio(p.getBio())
                .gender(p.getGender())
                .displayName(p.getDisplayName())
                .education(p.getEducation())
                .links(p.getLinks())
                .city(p.getCity())
                .avatarUrl(p.getAvatarUrl())
                .backgroundUrl(p.getBackgroundUrl())

                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .deletedAt(p.getDeletedAt())
                .deletedBy(p.getDeletedBy())
                .build();
    }

    private void send(UserProfileEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(PROFILE_EVENTS_TOPIC, event.getId(), json);
        } catch (JsonProcessingException e) {
            log.error("[Kafka] Serialize thất bại", e);
            throw new RuntimeException(e);
        }
    }

}
