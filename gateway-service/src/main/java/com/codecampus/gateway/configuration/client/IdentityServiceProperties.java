package com.codecampus.gateway.configuration.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "identity.service")
public class IdentityServiceProperties {

    /**
     * Base URL bao gồm context-path (/identity)
     * VD:
     * - Local : http://localhost:8080/identity
     * - Docker: http://identity-service:8080/identity
     */
    private String baseUrl;
}
