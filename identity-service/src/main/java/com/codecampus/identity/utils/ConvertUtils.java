package com.codecampus.identity.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ConvertUtils
{
  // Định dạng dd/MM/yyyy
  private static final DateTimeFormatter DMY_FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy");

  /**
   * Chuyển chuỗi ngày có định dạng dd/MM/yyyy sang Instant (đầu ngày).
   *
   * @param dateStr chuỗi ngày theo định dạng "dd/MM/yyyy", ví dụ "31/12/2024"
   * @return Instant tương ứng (thời điểm bắt đầu ngày đó) theo múi giờ hệ thống
   */
  public static Instant parseDdMmYyyyToInstant(String dateStr) {
    // Parse thành LocalDate
    LocalDate localDate = LocalDate.parse(dateStr, DMY_FORMATTER);
    // Chuyển sang Instant tại thời điểm 00:00 của ngày, theo múi giờ hệ thống
    return localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
  }
}
