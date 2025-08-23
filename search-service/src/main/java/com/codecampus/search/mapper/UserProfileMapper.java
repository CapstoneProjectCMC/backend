package com.codecampus.search.mapper;

import com.codecampus.search.dto.response.UserProfileResponse;
import com.codecampus.search.entity.UserProfileDocument;
import com.codecampus.search.utils.ConvertUtils;
import events.user.UserRegisteredEvent;
import events.user.data.UserPayload;
import events.user.data.UserProfileCreationPayload;
import events.user.data.UserProfilePayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring", uses = {ConvertUtils.class})
public interface UserProfileMapper {

    @Mapping(source = "dob", target = "dob", qualifiedByName = "instantToDdMmYyyyUTC")
    UserProfileResponse toUserProfileResponseFromUserProfileDocument(
            UserProfileDocument userProfileDocument);

    default UserProfileDocument toUserProfileDocumentFromUserRegisteredEvent(
            UserRegisteredEvent userRegisteredEvent) {
        UserPayload userPayload = userRegisteredEvent.getUser();
        UserProfileCreationPayload profilePayload =
                userRegisteredEvent.getProfile();

        return UserProfileDocument.builder()
                .userId(userRegisteredEvent.getId())
                .username(userPayload.getUsername())
                .email(userPayload.getEmail())
                .active(userPayload.isActive())
                .roles(userPayload.getRoles())
                .firstName(profilePayload.getFirstName())
                .lastName(profilePayload.getLastName())
                .dob(profilePayload.getDob())
                .bio(profilePayload.getBio())
                .gender(profilePayload.getGender())
                .displayName(profilePayload.getDisplayName())
                .education(profilePayload.getEducation())
                .links(profilePayload.getLinks())
                .city(profilePayload.getCity())
                .createdAt(userPayload.getCreatedAt() != null ?
                        userPayload.getCreatedAt() :
                        Instant.now())
                .updatedAt(userPayload.getUpdatedAt() != null ?
                        userPayload.getUpdatedAt() :
                        Instant.now())
                .deletedAt(null)
                .deletedBy(null)
                .build();
    }

    default void updateUserPayloadToUserProfileDocument(
            UserPayload userPayload,
            UserProfileDocument userProfileDocument) {
        if (userPayload == null || userProfileDocument == null) {
            return;
        }
        userProfileDocument.setUsername(userPayload.getUsername());
        userProfileDocument.setEmail(userPayload.getEmail());
        userProfileDocument.setActive(userPayload.isActive());
        userProfileDocument.setRoles(userPayload.getRoles());
        userProfileDocument.setUpdatedAt(userPayload.getUpdatedAt());
    }

    default void updateUserProfilePayloadToUserProfileDocument(
            UserProfilePayload userProfilePayload,
            UserProfileDocument userProfileDocument) {
        if (userProfilePayload == null || userProfileDocument == null) {
            return;
        }
        userProfileDocument.setFirstName(userProfilePayload.getFirstName());
        userProfileDocument.setLastName(userProfilePayload.getLastName());
        userProfileDocument.setDob(userProfilePayload.getDob());
        userProfileDocument.setBio(userProfilePayload.getBio());
        userProfileDocument.setGender(userProfilePayload.getGender());
        userProfileDocument.setDisplayName(userProfilePayload.getDisplayName());
        userProfileDocument.setEducation(userProfilePayload.getEducation());
        userProfileDocument.setLinks(userProfilePayload.getLinks());
        userProfileDocument.setCity(userProfilePayload.getCity());
        userProfileDocument.setAvatarUrl(userProfilePayload.getAvatarUrl());
        userProfileDocument.setBackgroundUrl(
                userProfilePayload.getBackgroundUrl());
        userProfileDocument.setUpdatedAt(
                userProfilePayload.getUpdatedAt() != null ?
                        userProfilePayload.getUpdatedAt() : Instant.now());

        // Cho phép đồng bộ identity snapshot nếu profile-service gửi kèm
        if (userProfilePayload.getUsername() != null) {
            userProfileDocument.setUsername(userProfilePayload.getUsername());
        }
        if (userProfilePayload.getEmail() != null) {
            userProfileDocument.setEmail(userProfilePayload.getEmail());
        }
        if (userProfilePayload.getRoles() != null) {
            userProfileDocument.setRoles(userProfilePayload.getRoles());
        }
        if (userProfilePayload.isActive()) {
            userProfileDocument.setActive(userProfilePayload.isActive());
        }
    }
}