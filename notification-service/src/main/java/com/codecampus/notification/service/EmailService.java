package com.codecampus.notification.service;

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
public class EmailService {
  JavaMailSender mailSender;

  @Value("${spring.mail.username:}")
  @NonFinal
  String fromEmail;

  public void send(String to, String subject, String body) {
    try {
      SimpleMailMessage msg = new SimpleMailMessage();
      if (fromEmail != null && !fromEmail.isBlank()) {
        msg.setFrom(fromEmail);
      }
      msg.setTo(to);
      msg.setSubject(subject != null ? subject : "Notification");
      msg.setText(body != null ? body : "");
      mailSender.send(msg);
      log.info("Sent email to {}", to);
    } catch (Exception e) {
      log.error("Send mail failed to {}: {}", to, e.getMessage(), e);
      throw e;
    }
  }
}