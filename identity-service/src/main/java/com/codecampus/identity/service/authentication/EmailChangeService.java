package com.codecampus.identity.service.authentication;

import com.codecampus.identity.dto.request.authentication.ChangeEmailRequest;
import com.codecampus.identity.dto.request.authentication.ChangeEmailVerifyRequest;
import com.codecampus.identity.entity.account.OtpVerification;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.helper.AuthenticationHelper;
import com.codecampus.identity.repository.account.OtpVerificationRepository;
import com.codecampus.identity.repository.account.UserRepository;
import com.codecampus.identity.service.kafka.UserEventProducer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailChangeService {

    OtpVerificationRepository otpVerificationRepository;
    UserRepository userRepository;
    UserEventProducer userEventProducer;

    JavaMailSender javaMailSender;

    @Value("${app.otp.expiry-minutes}")
    @NonFinal
    protected int otpExpiryMinutes;

    public void requestChangeEmail(
            ChangeEmailRequest changeEmailRequest) {
        // Không cho trùng email
        if (userRepository.existsByEmail(changeEmailRequest.getNewEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String otpCode = generateOtp();
        Instant expiryTime = Instant.now()
                .plus(otpExpiryMinutes, ChronoUnit.MINUTES);

        OtpVerification otpVerification = otpVerificationRepository.findByEmail(
                        changeEmailRequest.getNewEmail())
                .map(o -> {
                    o.setOtpCode(otpCode);
                    o.setExpiryTime(expiryTime);
                    o.setVerified(false);
                    return o;
                })
                .orElseGet(() -> OtpVerification.builder()
                        .email(changeEmailRequest.getNewEmail())
                        .otpCode(otpCode)
                        .expiryTime(expiryTime)
                        .verified(false)
                        .build());
        otpVerificationRepository.save(otpVerification);

        sendEmail(changeEmailRequest.getNewEmail(), otpCode);
    }

    public void verifyOtp(ChangeEmailVerifyRequest changeEmailVerifyRequest) {
        OtpVerification otpVerification = otpVerificationRepository.findByEmail(
                        changeEmailVerifyRequest.getNewEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        if (!otpVerification.getOtpCode()
                .equals(changeEmailVerifyRequest.getOtpCode())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        if (Instant.now().isAfter(otpVerification.getExpiryTime())) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        // Cập nhật email cho user hiện tại
        User user = userRepository.findById(AuthenticationHelper.getMyUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setEmail(changeEmailVerifyRequest.getNewEmail());
        userRepository.save(user);
        userEventProducer.publishUpdatedUserEvent(user);

        otpVerification.setVerified(true);
        otpVerificationRepository.save(otpVerification);
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void sendEmail(String email, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Xác minh đổi email");
        message.setText(
                "Mã OTP để xác minh email mới: " + otpCode + "\nHiệu lực: "
                        + otpExpiryMinutes + " phút.");
        javaMailSender.send(message);
    }
}
