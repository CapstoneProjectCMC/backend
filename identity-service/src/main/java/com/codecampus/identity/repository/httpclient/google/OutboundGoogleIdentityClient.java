package com.codecampus.identity.repository.httpclient.google;

import com.codecampus.identity.configuration.feign.FeignConfigForm;
import com.codecampus.identity.dto.request.authentication.ExchangeTokenRequest;
import com.codecampus.identity.dto.response.authentication.ExchangeTokenResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
    name = "outbound-identity",
    url = "https://oauth2.googleapis.com",
    configuration = {FeignConfigForm.class}
)
public interface OutboundGoogleIdentityClient {
  @PostMapping(
      value = "/token",
      produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  ExchangeTokenResponse exchangeToken(
      @QueryMap ExchangeTokenRequest exchangeTokenRequest);
}
