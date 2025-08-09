package com.codecampus.identity.helper;

import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.profile.UserProfileCreationRequest;
import com.codecampus.identity.dto.request.profile.UserProfileUpdateRequest;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.mapper.client.UserProfileMapper;
import com.codecampus.identity.repository.httpclient.profile.ProfileClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileSyncHelper {
    ProfileClient profileClient;
    UserProfileMapper userProfileMapper;

    /**
     * Gửi request tạo profile. Nếu đã tồn tại profile → bỏ qua, không ném lỗi.
     */
    public void createProfile(
            User user,
            UserCreationRequest userCreationRequest) {
        UserProfileCreationRequest userProfileCreationRequest =
                userProfileMapper
                        .toUserProfileCreationRequestFromUserCreationRequest(
                                userCreationRequest);
        userProfileCreationRequest.setUserId(user.getId());

        try {
            profileClient.internalCreateUserProfile(userProfileCreationRequest);
        } catch (FeignException.Conflict ignored) {
            // profile đã tồn tại ⇒ idempotent
        }
    }

    public void updateProfile(
            String userId,
            UserProfileUpdateRequest userProfileUpdateRequest) {
        try {
            profileClient.internalUpdateProfileByUserId(userId,
                    userProfileUpdateRequest);
        } catch (FeignException.Conflict ignored) {
            // profile đã tồn tại ⇒ idempotent
        }
    }

    public void softDeleteProfile(
            String userId,
            String deletedBy) {
        try {
            profileClient.internalSoftDeleteByUserId(userId, deletedBy);
        } catch (FeignException.Conflict ignored) {
            // profile đã tồn tại ⇒ idempotent
        }
    }

    public void restoreProfile(String userId) {
        try {
            profileClient.internalRestoreProfile(userId);
        } catch (FeignException.Conflict ignored) {
            // profile đã tồn tại ⇒ idempotent
        }
    }
}
