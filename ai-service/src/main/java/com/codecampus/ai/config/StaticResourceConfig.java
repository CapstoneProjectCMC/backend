package com.codecampus.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /files/** -> thư mục upload-dir
        // Ví dụ: GET /ai/files/2025/08/21/abc.png  ->  <uploadDir>/2025/08/21/abc.png
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + ensureTrailingSlash(uploadDir))
                .setCacheControl(
                        CacheControl.maxAge(Duration.ofDays(30)).cachePublic());
    }

    private String ensureTrailingSlash(String path) {
        return path.endsWith("/") ? path : path + "/";
    }
}
