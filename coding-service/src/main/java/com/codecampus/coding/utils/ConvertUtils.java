package com.codecampus.coding.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Named;

/**
 * Tiện ích chuyển đổi giữa định dạng chuỗi ngày "dd/MM/yyyy"
 * và đối tượng {@link Instant}.
 *
 * <p>Cung cấp các phương thức:
 * <ul>
 *   <li>{@link #parseDdMmYyyyToInstant(String)}: Chuyển chuỗi ngày sang {@code Instant} tại đầu ngày (00:00 UTC).</li>
 *   <li>{@link #formatInstantToDdMmYyyyUtc(Instant)}: Định dạng {@code Instant} sang chuỗi ngày theo UTC.</li>
 *   <li>{@link #formatInstantToDdMmYyyyLocal(Instant)}: Định dạng {@code Instant} sang chuỗi ngày theo múi giờ hệ thống.</li>
 * </ul>
 * </p>
 */
public class ConvertUtils {
  // Định dạng dd/MM/yyyy
  private static final DateTimeFormatter DMY_FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy");

  /**
   * Chuyển chuỗi ngày có định dạng dd/MM/yyyy sang Instant (đầu ngày).
   *
   * @param dateStr chuỗi ngày theo định dạng "dd/MM/yyyy", ví dụ "31/12/2024"
   * @return Instant tương ứng (thời điểm bắt đầu ngày đó) theo múi giờ hệ thống
   */
  @Named("DdMmYyyyToInstant")
  public static Instant parseDdMmYyyyToInstant(String dateStr) {
    if (dateStr == null || dateStr.isBlank()) {
      return null;
    }

    // Parse thành LocalDate
    LocalDate localDate = LocalDate.parse(dateStr, DMY_FORMATTER);
    // Chuyển sang Instant tại thời điểm 00:00 của ngày, theo múi giờ hệ thống
    return localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
  }

  /**
   * Chuyển Instant về chuỗi ngày "dd/MM/yyyy" tính theo UTC.
   *
   * @param instant thời điểm cần format
   * @return chuỗi ngày dạng "dd/MM/yyyy"
   */
  @Named("instantToDdMmYyyyUTC")
  public static String formatInstantToDdMmYyyyUtc(Instant instant) {
    if (instant == null) {
      return null;
    }

    return instant
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .format(DMY_FORMATTER);
  }

  /**
   * Chuyển Instant về chuỗi ngày "dd/MM/yyyy" tính theo múi giờ hệ thống.
   *
   * @param instant thời điểm cần format
   * @return chuỗi ngày dạng "dd/MM/yyyy"
   */
  public static String formatInstantToDdMmYyyyLocal(Instant instant) {
    if (instant == null) {
      return null;
    }

    return instant
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DMY_FORMATTER);
  }
}
