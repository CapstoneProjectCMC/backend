package com.codecampus.identity.service.authentication;

import com.codecampus.identity.dto.request.authentication.ForgotPasswordRequest;
import com.codecampus.identity.dto.request.authentication.ResetPasswordRequest;
import com.codecampus.identity.dto.request.authentication.VerifyPasswordResetRequest;
import com.codecampus.identity.dto.response.authentication.OtpResponse;
import com.codecampus.identity.entity.account.PasswordResetToken;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.repository.account.PasswordResetTokenRepository;
import com.codecampus.identity.repository.account.UserRepository;
import com.codecampus.identity.service.kafka.NotificationEventProducer;
import com.codecampus.identity.service.kafka.UserEventProducer;
import events.notification.NotificationEvent;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Random;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PasswordResetService {
  JavaMailSender mailSender;
  UserRepository userRepository;
  PasswordResetTokenRepository tokenRepository;
  PasswordEncoder passwordEncoder;
  UserEventProducer userEventProducer;
  NotificationEventProducer notificationEventProducer;

  @Value("${spring.mail.username}")
  @NonFinal
  String fromEmail;

  @Value("${app.password-reset.expiry-minutes:15}")
  @NonFinal
  int expiryMinutes;

  public OtpResponse requestReset(ForgotPasswordRequest request) {
    // Tìm user theo email
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

    // Tạo OTP
    String otp = genOtp();
    Instant expiry = Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES);

    // Upsert token theo email (mỗi email 1 record đang hiệu lực)
    PasswordResetToken token = tokenRepository.findByEmail(user.getEmail())
        .map(t -> {
          t.setOtpCode(otp);
          t.setExpiryTime(expiry);
          t.setConsumed(false);
          return t;
        })
        .orElse(PasswordResetToken.builder()
            .email(user.getEmail())
            .otpCode(otp)
            .expiryTime(expiry)
            .consumed(false)
            .build());

    tokenRepository.save(token);

    // Gửi mail
    sendEmail(user.getEmail(), otp);

    return OtpResponse.builder()
        .email(user.getEmail())
        .message("Password reset OTP sent successfully")
        .build();
  }

  public void verifyOtp(VerifyPasswordResetRequest request) {
    PasswordResetToken token = tokenRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

    if (token.isConsumed()) {
      throw new AppException(ErrorCode.INVALID_OTP);
    }
    if (!token.getOtpCode().equals(request.getOtp())) {
      throw new AppException(ErrorCode.INVALID_OTP);
    }
    if (Instant.now().isAfter(token.getExpiryTime())) {
      throw new AppException(ErrorCode.OTP_EXPIRED);
    }
    // Hợp lệ => không đổi pass, chỉ xác thực OK
  }

  public void resetPassword(ResetPasswordRequest request) {
    PasswordResetToken token = tokenRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

    if (token.isConsumed()) {
      throw new AppException(ErrorCode.INVALID_OTP);
    }
    if (!token.getOtpCode().equals(request.getOtp())) {
      throw new AppException(ErrorCode.INVALID_OTP);
    }
    if (Instant.now().isAfter(token.getExpiryTime())) {
      throw new AppException(ErrorCode.OTP_EXPIRED);
    }

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
    userEventProducer.publishUpdatedUserEvent(user);

    token.setConsumed(true);
    tokenRepository.save(token);

    notificationEventProducer.publish(NotificationEvent.builder()
        .channel("SOCKET")
        .recipient(user.getId())
        .templateCode("PASSWORD_RESET_SUCCESS")
        .param(Map.of())
        .subject("Đặt lại mật khẩu thành công")
        .body("Mật khẩu của bạn đã được thay đổi.")
        .build()
    );
  }

  /* helpers */

  String genOtp() {
    return String.format("%06d", new Random().nextInt(999999));
  }

  void sendEmail(String email, String otpCode) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(fromEmail);
      message.setTo(email);
      message.setSubject("Đặt lại mật khẩu");
      message.setText("Mã OTP đặt lại mật khẩu của bạn là: " + otpCode
          + "\nMã có hiệu lực trong " + expiryMinutes + " phút");
      mailSender.send(message);
    } catch (Exception e) {
      log.error("Failed to send password reset email", e);
      throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
    }
  }
}
