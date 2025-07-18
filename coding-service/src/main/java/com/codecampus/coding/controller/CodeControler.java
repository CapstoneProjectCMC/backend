package com.codecampus.coding.controller;

import com.codecampus.coding.dto.common.ApiResponse;
import com.codecampus.coding.service.CodeService;
import com.google.protobuf.Api;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/code")
public class CodeControler
{
  @Autowired
  public CodeService codeService;

  @GetMapping("/compile")
  public ResponseEntity<ApiResponse<?>> compileCode()
  {
    String powerShellresponse = codeService.compileCode();
    return ResponseEntity.ok(ApiResponse.builder().result(powerShellresponse).build());
  }
}
