package com.codecampus.identity.configuration.config;

import com.codecampus.identity.dto.request.authentication.IntrospectRequest;
import com.codecampus.identity.service.authentication.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import java.text.ParseException;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

/**
 * JwtDecoder tùy chỉnh để xác thực token JWT thông qua API introspect
 * trước khi giải mã token nội bộ bằng NimbusJwtDecoder.
 * 1. Gửi yêu cầu introspect đến AuthenticationService để kiểm tra tính hợp lệ của token.
 * 2. Nếu token hợp lệ, khởi tạo NimbusJwtDecoder với signerKey cấu hình và giải mã token.
 */
@Component
public class CustomJwtDecoder implements JwtDecoder
{
  @Value("${app.jwt.signerKey}")
  private String signerKey;

  @Autowired
  private AuthenticationService authenticationService;

  private NimbusJwtDecoder nimbusJwtDecoder = null;


  /**
   * Giải mã token JWT.
   * <ol>
   *   <li>Gọi AuthenticationService.introspect để kiểm tra token hợp lệ.</li>
   *   <li>Nếu introspect trả về không hợp lệ hoặc có lỗi, ném JwtException.</li>
   *   <li>Nếu lần đầu giải mã, khởi tạo NimbusJwtDecoder với algorithm HS512.</li>
   *   <li>Sử dụng NimbusJwtDecoder để decode token và trả về đối tượng Jwt.</li>
   * </ol>
   *
   * @param token chuỗi JWT cần giải mã và xác thực
   * @return đối tượng Jwt chứa thông tin đã giải mã của token
   * @throws JwtException nếu token không hợp lệ hoặc quá trình decode gặp lỗi
   */
  @Override
  public Jwt decode(String token) throws JwtException
  {
    try {
      var response = authenticationService.introspect(
          IntrospectRequest.builder()
              .token(token)
              .build());

      if (!response.isValid()) {
        throw new JwtException("Token invalid");
      }
    } catch (JOSEException | ParseException e)
    {
      throw new JwtException(e.getMessage());
    }

    if (Objects.isNull(nimbusJwtDecoder)) {
      SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");

      nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
          .macAlgorithm(MacAlgorithm.HS512)
          .build();
    }

    return nimbusJwtDecoder.decode(token);
  }
}
