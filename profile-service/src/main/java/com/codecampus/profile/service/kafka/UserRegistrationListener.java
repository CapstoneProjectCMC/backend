package com.codecampus.profile.service.kafka;

import com.codecampus.profile.constant.social.OrgRole;
import com.codecampus.profile.entity.Org;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.entity.properties.organization.MemberOrg;
import com.codecampus.profile.repository.OrgRepository;
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
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRegistrationListener {

    UserProfileRepository userProfileRepository;
    OrgRepository orgRepository;
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
        profile.setRoles(u.getRoles());
        profile.setUpdatedAt(u.getUpdatedAt() != null ? u.getUpdatedAt() :
                Instant.now());

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

        if (p.getOrganizationId() != null && !p.getOrganizationId().isBlank()) {

            // Upsert Organization node (nếu chưa có)
            Org org = orgRepository
                    .findByOrgId(p.getOrganizationId())
                    .orElseGet(() -> orgRepository.save(
                            Org.builder()
                                    .orgId(p.getOrganizationId())
                                    .build()
                    ));

            // Tránh tạo trùng quan hệ nếu message bị replay
            boolean exists = profile
                    .getMemberOrgs()
                    .stream()
                    .anyMatch(m ->
                            m.getOrganization() != null
                                    && p.getOrganizationId()
                                    .equals(m.getOrganization().getOrgId())
                    );

            if (!exists) {
                MemberOrg member = MemberOrg.builder()
                        .joinAt(Instant.now())
                        .build();

                // set role nếu có (không có thì giữ default STUDENT trong entity)
                String roleRaw = p.getOrganizationMemberRole();
                if (roleRaw != null && !roleRaw.isBlank()) {
                    try {
                        OrgRole parsed =
                                OrgRole.valueOf(roleRaw.trim()
                                        .toUpperCase(Locale.ROOT));
                        member.setMemberRole(parsed);
                    } catch (IllegalArgumentException ignored) {
                        // fallback giữ default STUDENT
                    }
                }

                member.setOrganization(org);
                profile.getMemberOrgs().add(member);
            }
        }

        // nếu đang bị soft-delete mà nhận được registration (enabled=true) -> auto-restore
        if (profile.getDeletedAt() != null &&
                Boolean.TRUE.equals(profile.getActive())) {
            profile.setDeletedAt(null);
            profile.setDeletedBy(null);
        }

        userProfileRepository.save(profile);
    }
}
