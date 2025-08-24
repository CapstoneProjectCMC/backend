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
    String username;
    String email;
    String displayName;
    String avatarUrl;
    String backgroundUrl;
    Boolean active;
    String firstName;
    String lastName;
    Boolean gender;
}
