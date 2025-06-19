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

/**
 * Dịch vụ quản lý và xác thực mã OTP (One-Time Password).
 *
 * <p>Cung cấp các phương thức:
 * <ul>
 *   <li>sendOtp: Tạo và gửi mã OTP qua email.</li>
 *   <li>verifyOtp: Kiểm tra tính hợp lệ và hết hạn của mã OTP, kích hoạt tài khoản người dùng.</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpService {
  JavaMailSender mailSender;
  OtpVerificationRepository otpRepository;
  UserRepository userRepository;

  @Value("${app.otp.expiry-minutes}")
  @NonFinal
  protected int otpExpiryMinutes;

  @Value("${spring.mail.username}")
  @NonFinal
  protected String fromEmail;

  /**
   * Tạo và gửi mã OTP cho địa chỉ email trong UserCreationRequest.
   *
   * <p>Quy trình:
   * <ol>
   *   <li>Tạo mã OTP ngẫu nhiên 6 chữ số.</li>
   *   <li>Xác định thời gian hết hạn = thời điểm hiện tại + otpExpiryMinutes.</li>
   *   <li>Lưu hoặc cập nhật bản ghi OtpVerification trong database.</li>
   *   <li>Gửi email chứa OTP đến người dùng.</li>
   *   <li>Trả về OtpResponse chứa email và thông báo thành công.</li>
   * </ol>
   * </p>
   *
   * @param request thông tin người dùng (chứa email) để gửi OTP
   * @return OtpResponse chứa email và message
   */
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

  /**
   * Xác thực mã OTP và kích hoạt tài khoản người dùng.
   *
   * <p>Quy trình:
   * <ol>
   *   <li>Lấy bản ghi OTP theo email, nếu không có ném AppException EMAIL_NOT_FOUND.</li>
   *   <li>So sánh otpCode, nếu không khớp ném AppException INVALID_OTP.</li>
   *   <li>Kiểm tra thời gian hiện tại so với expiryTime,
   *       nếu đã quá hạn ném AppException OTP_EXPIRED.</li>
   *   <li>Tìm user theo email, nếu không có ném AppException USER_NOT_FOUND.</li>
   *   <li>Đánh dấu user.setEnabled(true) và lưu lại.</li>
   * </ol>
   * </p>
   *
   * @param request chứa email và otpCode cần xác thực
   * @throws AppException khi email không tồn tại, OTP sai hoặc hết hạn
   */
  public void verifyOtp(OtpVerificationRequest request) {
    OtpVerification otp = otpRepository
        .findByEmail(request.getEmail())
        .orElseThrow(
            () -> new AppException(ErrorCode.EMAIL_NOT_FOUND)
        );

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

  /**
   * Tạo mã OTP 6 chữ số ngẫu nhiên.
   *
   * @return chuỗi 6 chữ số
   */
  private String generateOtp() {
    Random random = new Random();
    return String.format("%06d", random.nextInt(999999));
  }

  /**
   * Gửi email chứa mã OTP đến địa chỉ người dùng.
   *
   * @param email   địa chỉ nhận OTP
   * @param otpCode mã OTP
   * @throws AppException nếu gửi email thất bại
   */
  private void sendEmail(String email, String otpCode) {
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
