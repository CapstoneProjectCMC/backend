//package com.codecampus.identity.controller.authentication;
//
//import com.codecampus.identity.dto.common.ApiResponse;
//import com.codecampus.identity.dto.request.authentication.ChangeEmailRequest;
//import com.codecampus.identity.dto.request.authentication.ChangeEmailVerifyRequest;
//import com.codecampus.identity.service.authentication.EmailChangeService;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Builder
//@Slf4j
//@RequestMapping("/user/email")
//public class EmailChangeController {
//
//    EmailChangeService emailChangeService;
//
//    @PostMapping("/change/request")
//    public ApiResponse<Void> request(
//            @RequestBody ChangeEmailRequest changeEmailRequest) {
//        emailChangeService.requestChangeEmail(changeEmailRequest);
//        return ApiResponse.<Void>builder()
//                .message("OTP đã gửi tới email mới")
//                .build();
//    }
//
//    @PostMapping("/change/verify")
//    public ApiResponse<Void> verify(
//            @RequestBody ChangeEmailVerifyRequest changeEmailVerifyRequest) {
//        emailChangeService.verifyOtp(changeEmailVerifyRequest);
//        return ApiResponse.<Void>builder()
//                .message("Đổi email thành công")
//                .build();
//    }
//}
