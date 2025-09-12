package com.codecampus.organization.configuration.config;

import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
public class CustomJwtDecoder implements JwtDecoder {
  /**
   * Giải mã chuỗi JWT và trả về đối tượng Jwt chứa thông tin claims.
   *
   * @param token chuỗi JWT đã được ký (không bao gồm tiền tố Bearer)
   * @return đối tượng Jwt chứa token gốc, thời gian phát hành, hết hạn, header và claims
   * @throws JwtException nếu token không hợp lệ hoặc không parse được
   */
  @Override
  public Jwt decode(String token) throws JwtException {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      return new Jwt(
          token,
          signedJWT.getJWTClaimsSet().getIssueTime().toInstant(),
          signedJWT.getJWTClaimsSet().getExpirationTime().toInstant(),
          signedJWT.getHeader().toJSONObject(),
          signedJWT.getJWTClaimsSet().getClaims()
      );
    } catch (ParseException e) {
      throw new JwtException("Invalid token");
    }
  }
}