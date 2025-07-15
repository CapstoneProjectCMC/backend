package com.codecampus.quiz.configuration.grpc;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.stream.Collectors;

/**
 * Cung cấp GrpcAuthenticationReader để framework gRPC-Security
 * có thể xác thực Bearer token trong metadata.
 */
@Configuration
@RequiredArgsConstructor
public class GrpcJwtSecurityConfig {

    // gRPC chuyển header-key thành ASCII-lowercase
    private static final Metadata.Key<String> AUTHORIZATION =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);
    private final JwtDecoder jwtDecoder;

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {

        return new GrpcAuthenticationReader() {

            /**
             * @return Authentication nếu header hợp lệ, <b>null</b> nếu không có token
             */
            @Override
            public @Nullable Authentication readAuthentication(
                    ServerCall<?, ?> call, Metadata headers) {

                String header = headers.get(AUTHORIZATION);
                if (header == null || !header.startsWith("Bearer ")) {
                    return null;                          // anonymous
                }

                String token = header.substring(7);
                Jwt jwt = jwtDecoder.decode(token);

                var authorities = jwt.getClaimAsStringList("roles").stream()
                        .map(r -> "ROLE_" + r.toUpperCase())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());

                return new JwtAuthenticationToken(
                        jwt, authorities, jwt.getSubject());
            }
        };
    }
}
