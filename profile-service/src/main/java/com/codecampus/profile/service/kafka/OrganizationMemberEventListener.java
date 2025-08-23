package com.codecampus.profile.service.kafka;

import com.codecampus.constant.ScopeType;
import com.codecampus.profile.constant.social.OrgRole;
import com.codecampus.profile.entity.Org;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.entity.properties.organization.MemberOrg;
import com.codecampus.profile.repository.OrgRepository;
import com.codecampus.profile.repository.UserProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.org.OrganizationMemberEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationMemberEventListener {

    OrgRepository orgRepository;
    UserProfileRepository userRepo;
    ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${app.event.org-member-events}",
            groupId = "profile-service"
    )
    public void onOrgMemberEvent(String raw) {
        try {
            OrganizationMemberEvent event =
                    objectMapper.readValue(raw, OrganizationMemberEvent.class);
            String orgKey = toOrgIdFromScopeType(
                    event.getScopeType(), event.getScopeId());

            Optional<UserProfile> userOptional =
                    userRepo.findActiveByUserId(event.getUserId());
            if (userOptional.isEmpty()) {
                log.warn("User {} not found, skip membership {}",
                        event.getUserId(), raw);
                return;
            }

            UserProfile user = userOptional.get();
            Org org = orgRepository.findByOrgId(orgKey)
                    .orElseGet(() -> orgRepository.save(
                            Org.builder().orgId(orgKey).build()));

            switch (event.getType()) {
                case ADDED -> addIfAbsent(user, org, event);
                case UPDATED -> updateIfExists(user, org, event);
                case DELETED -> removeIfExists(user, orgKey);
            }
            userRepo.save(user);
        } catch (Exception e) {
            log.error("[Kafka] org-member-event parse/fail: {}", raw, e);
            throw new RuntimeException(e);
        }
    }

    void addIfAbsent(UserProfile user, Org org,
                     OrganizationMemberEvent event) {
        boolean exists = user.getMemberOrgs().stream()
                .anyMatch(m -> m.getOrganization() != null &&
                        org.getOrgId().equals(m.getOrganization().getOrgId()));
        if (!exists) {
            MemberOrg rel = MemberOrg.builder()
                    .organization(org)
                    .joinAt(event.getAt() != null ? event.getAt() :
                            Instant.now())
                    .memberRole(mapRole(event.getRole()))
                    .build();
            user.getMemberOrgs().add(rel);
        } else {
            updateIfExists(user, org, event);
        }
    }

    void updateIfExists(UserProfile user, Org org,
                        OrganizationMemberEvent event) {
        user.getMemberOrgs().stream()
                .filter(m -> m.getOrganization() != null &&
                        org.getOrgId().equals(m.getOrganization().getOrgId()))
                .findFirst()
                .ifPresent(m -> m.setMemberRole(mapRole(event.getRole())));
        // (isActive=false không lưu trong quan hệ; dùng REMOVED để xóa quan hệ)
    }

    void removeIfExists(UserProfile user, String orgKey) {
        user.getMemberOrgs().removeIf(m -> m.getOrganization() != null &&
                orgKey.equals(m.getOrganization().getOrgId()));
    }

    OrgRole mapRole(String raw) {
        if (raw == null) {
            return OrgRole.STUDENT;
        }
        return switch (raw.trim().toUpperCase(Locale.ROOT)) {
            case "SUPERADMIN" -> OrgRole.ADMIN; // hoặc SUPER_ADMIN nếu enum có
            case "ADMIN" -> OrgRole.ADMIN;
            case "TEACHER" -> OrgRole.TEACHER;
            default -> OrgRole.STUDENT;
        };
    }

    String toOrgIdFromScopeType(ScopeType scopeType, String id) {
        return switch (scopeType) {
            case ORGANIZATION -> "ORG:" + id;
            case GRADE -> "GRADE:" + id;
            case CLASS -> "CLASS:" + id;
        };
    }
}