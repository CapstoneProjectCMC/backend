package com.codecampus.identity.service.authentication;

import com.codecampus.identity.dto.request.authentication.OtpVerificationRequest;
import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.response.authentication.OtpResponse;
import com.codecampus.identity.entity.account.OtpVerification;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.repository.account.OtpVerificationRepository;
import com.codecampus.identity.repository.account.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpService
{
  JavaMailSender mailSender;
  OtpVerificationRepository otpRepository;
  UserRepository userRepository;

  @Value("${app.otp.expiry-minutes}")
  @NonFinal
  protected int otpExpiryMinutes;

  @Value("${spring.mail.username}")
  @NonFinal
  protected String fromEmail;

  public OtpResponse sendOtp(UserCreationRequest request) {
    // Tạo mã OTP ngẫu nhiên
    String otpCode = generateOtp();
    Instant expiryTime = Instant.now()
        .plus(otpExpiryMinutes, ChronoUnit.MINUTES);

    // Lưu hoặc cập nhật OTP trong database
    OtpVerification otp = otpRepository.findByEmail(request.getEmail())
        .map(existing -> {
          existing.setOtpCode(otpCode);
          existing.setExpiryTime(expiryTime);
          existing.setVerified(false);
          return existing;
        })
        .orElseGet(() -> OtpVerification.builder()
            .email(request.getEmail())
            .otpCode(otpCode)
            .expiryTime(expiryTime)
            .verified(false)
            .build()
        );
    otpRepository.save(otp);

    // Gửi email
    sendEmail(request.getEmail(), otpCode);

    return OtpResponse.builder()
        .email(request.getEmail())
        .message("OTP sent successfully")
        .build();
  }

  public void verifyOtp(OtpVerificationRequest request)
  {
    OtpVerification otp = otpRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new AppException(ErrorCode.OTP_NOT_FOUND));

    // Kiểm tra OTP
    if (!otp.getOtpCode().equals(request.getOtpCode())) {
      throw new AppException(ErrorCode.INVALID_OTP);
    }

    // Kiểm tra thời hạn
    if (Instant.now().isAfter(otp.getExpiryTime())) {
      throw new AppException(ErrorCode.OTP_EXPIRED);
    }

    // Kích hoạt tài khoản
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    user.setEnabled(true);
    userRepository.save(user);

    otpRepository.save(otp);
  }

  private String generateOtp() {
    Random random = new Random();
    return String.format("%06d", random.nextInt(999999));
  }

  private void sendEmail(String email, String otpCode)
  {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(fromEmail);
      message.setFrom(fromEmail);
      message.setTo(email);
      message.setSubject("Xác minh tài khoản");
      message.setText("Mã OTP của bạn là: " + otpCode
          + "\nMã có hiệu lực trong " + otpExpiryMinutes + " phút");
      mailSender.send(message);
      log.info("OTP sent to: {}", email);
    } catch (Exception e) {
      log.error("Failed to send OTP email", e);
      throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
    }
  }
}
