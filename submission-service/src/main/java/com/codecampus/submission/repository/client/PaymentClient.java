package com.codecampus.submission.repository.client;


import com.codecampus.submission.configuration.config.AuthenticationRequestInterceptor;
import com.codecampus.submission.dto.common.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "payment-service",
    url = "${app.services.payment}",
    path = "/internal",
    configuration = AuthenticationRequestInterceptor.class
)
public interface PaymentClient {

  @GetMapping("/purchase/check/{userId}")
  ApiResponse<Boolean> internalHasPurchased(
      @PathVariable("userId") String userId,
      @RequestParam("itemId") String itemId,
      @RequestParam("itemType") String itemType);

  // bulk check
  @PostMapping("/purchase/check/bulk/{userId}")
  ApiResponse<Map<String, Boolean>> internalHasPurchasedBulk(
      @PathVariable("userId") String userId,
      @RequestParam("itemType") String itemType,
      @RequestBody List<String> itemIds
  );
}