package com.codecampus.profile.service.kafka;

import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.repository.UserProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.user.UserRegisteredEvent;
import events.user.data.UserPayload;
import events.user.data.UserProfileCreationPayload;
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
public class UserRegistrationListener {

    UserProfileRepository userProfileRepository;
    ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${app.event.user-registrations}",
            groupId = "profile-service"
    )
    public void onRegistrationUser(String raw) {
        try {
            UserRegisteredEvent event =
                    objectMapper.readValue(raw, UserRegisteredEvent.class);
            upsertFromRegistration(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    void upsertFromRegistration(UserRegisteredEvent event) {
        UserPayload u = event.getUser();
        UserProfileCreationPayload p = event.getProfile();

        UserProfile profile = userProfileRepository.findByUserId(u.getUserId())
                .orElseGet(() -> UserProfile.builder()
                        .userId(u.getUserId())
                        .createdAt(u.getCreatedAt() != null ? u.getCreatedAt() :
                                Instant.now())
                        .build());

        // fields từ UserPayload
        profile.setUsername(u.getUsername());
        profile.setEmail(u.getEmail());
        profile.setActive(u.isActive());

        // fields profile chi tiết
        profile.setFirstName(p.getFirstName());
        profile.setLastName(p.getLastName());
        profile.setDob(p.getDob());
        profile.setBio(p.getBio());
        profile.setGender(p.getGender());
        profile.setDisplayName(p.getDisplayName());
        profile.setEducation(p.getEducation());
        profile.setLinks(p.getLinks());
        profile.setCity(p.getCity());

        // nếu đang bị soft-delete mà nhận được registration (enabled=true) -> auto-restore
        if (profile.getDeletedAt() != null &&
                Boolean.TRUE.equals(profile.getActive())) {
            profile.setDeletedAt(null);
            profile.setDeletedBy(null);
        }

        userProfileRepository.save(profile);
    }
}
