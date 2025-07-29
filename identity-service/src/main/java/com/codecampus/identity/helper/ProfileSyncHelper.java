package com.codecampus.identity.helper;

import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.request.profile.UserProfileCreationRequest;
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
            UserCreationRequest src) {
        UserProfileCreationRequest req =
                userProfileMapper.toUserProfileCreationRequest(src);
        req.setUserId(user.getId());

        try {
            profileClient.createUserProfile(req);
        } catch (FeignException.Conflict ignored) {
            // profile đã tồn tại ⇒ idempotent
        }
    }
}
