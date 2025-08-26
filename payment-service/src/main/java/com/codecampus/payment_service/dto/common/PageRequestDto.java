package com.codecampus.payment_service.dto.common;

import lombok.Data;

@Data
public class PageRequestDto {
  private int page;
  private int size;
  private String sortBy;
  private String sortDirection;
}
