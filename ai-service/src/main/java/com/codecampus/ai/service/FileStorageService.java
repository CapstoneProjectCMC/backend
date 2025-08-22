package com.codecampus.ai.service;

import com.codecampus.ai.dto.response.StoredFile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileStorageService {

    @Value("${app.public-base-url}")
    @NonFinal
    String publicBaseUrl;

    @Value("${app.file.upload-dir}")
    @NonFinal
    String uploadDir;

    public StoredFile store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File rỗng!");
        }
        try {
            // folder theo ngày: yyyy/MM/dd
            LocalDate today = LocalDate.now();
            Path base = Path.of(uploadDir,
                    String.valueOf(today.getYear()),
                    String.format("%02d", today.getMonthValue()),
                    String.format("%02d", today.getDayOfMonth()));
            Files.createDirectories(base);

            // tên file an toàn + UUID
            String cleanName = StringUtils.cleanPath(
                    file.getOriginalFilename() == null ? "file" :
                            file.getOriginalFilename());
            String ext = "";
            int dot = cleanName.lastIndexOf('.');
            if (dot > -1) {
                ext = cleanName.substring(dot);
            }
            String newName =
                    UUID.randomUUID().toString().replace("-", "") + ext;

            Path target = base.resolve(newName);
            Files.copy(file.getInputStream(), target,
                    StandardCopyOption.REPLACE_EXISTING);

            // URL public dưới /files/**
            String relative = today.getYear() + "/" +
                    String.format("%02d", today.getMonthValue()) + "/" +
                    String.format("%02d", today.getDayOfMonth()) + "/" +
                    newName;

            // NEW: Ưu tiên dùng app.public-base-url nếu có, fallback về context hiện tại
            String baseUrl = StringUtils.hasText(publicBaseUrl)
                    ? trimTrailingSlash(publicBaseUrl)
                    : trimTrailingSlash(
                    ServletUriComponentsBuilder.fromCurrentContextPath()
                            .toUriString());

            String publicUrl = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/files/")
                    .path(relative)
                    .build()
                    .toString();

            return new StoredFile(publicUrl, cleanName, file.getContentType(),
                    target.toAbsolutePath().toString());
        } catch (IOException ex) {
            log.error("Lỗi lưu file", ex);
            throw new RuntimeException("Không thể lưu file", ex);
        }
    }

    String trimTrailingSlash(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }
}