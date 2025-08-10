package com.codecampus.identity.dto.request.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeEmailVerifyRequest {
    String newEmail;
    String otpCode;
}
