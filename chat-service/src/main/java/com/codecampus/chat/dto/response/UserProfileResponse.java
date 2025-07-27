package com.codecampus.chat.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// Mặc định cho phép response cả null khi Dev
// Khi build thì KHÔNG response null
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {
    String userId;
    String displayName;
    String firstName;
    String lastName;
    String avatarUrl;
    String backgroundUrl;
}
