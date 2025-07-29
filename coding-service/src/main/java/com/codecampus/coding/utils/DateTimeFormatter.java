package com.codecampus.coding.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Tiện ích định dạng ngày giờ (DateTime) cho các thông báo hiển thị tương đối.
 *
 * <p>Cung cấp phương thức format(Instant) để trả về chuỗi mô tả khoảng thời gian đã trôi qua:
 * <ul>
 *   <li>Nếu dưới 60 giây, hiển thị "x second(s) ago".</li>
 *   <li>Nếu dưới 60 phút, hiển thị "x minute(s) ago".</li>
 *   <li>Nếu dưới 24 giờ, hiển thị "x hour(s) ago".</li>
 *   <li>Nếu lớn hơn hoặc bằng 24 giờ, hiển thị ngày theo định dạng ISO (YYYY-MM-DD).</li>
 * </ul>
 * </p>
 */
@Component
public class DateTimeFormatter {
    /**
     * Bản đồ chiến lược format dựa trên ngưỡng thời gian (giây).
     * Key: số giây tối đa, Value: hàm format tương ứng.
     */
    Map<Long, Function<Instant, String>> strategyMap = new LinkedHashMap<>();

    /**
     * Khởi tạo bản đồ chiến lược với các ngưỡng:
     * <ul>
     *   <li>60 giây → formatInSeconds()</li>
     *   <li>3600 giây (60 phút) → formatInMinutes()</li>
     *   <li>86400 giây (24 giờ) → formatInHours()</li>
     *   <li>Long.MAX_VALUE → formatInDate()</li>
     * </ul>
     */
    public DateTimeFormatter() {
        strategyMap.put(60L, this::formatInSeconds);
        strategyMap.put(3600L, this::formatInMinutes);
        strategyMap.put(86400L, this::formatInHours);
        strategyMap.put(Long.MAX_VALUE, this::formatInDate);
    }

    /**
     * Định dạng Instant thành chuỗi mô tả khoảng thời gian đã trôi qua.
     *
     * @param instant thời điểm cần định dạng
     * @return chuỗi mô tả thời gian (ví dụ "5 minute(s) ago" hoặc "2025-06-14")
     */
    public String format(Instant instant) {
        long elapseSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());

        var strategy = strategyMap.entrySet()
                .stream()
                .filter(longFunctionEntry -> elapseSeconds <
                        longFunctionEntry.getKey())
                .findFirst().get();
        return strategy.getValue().apply(instant);
    }

    /**
     * Format khoảng thời gian tính theo giây.
     *
     * @param instant thời điểm cần so sánh
     * @return "x second(s) ago"
     */
    private String formatInSeconds(Instant instant) {
        long elapseSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());
        return String.format("%s second(s) ago", elapseSeconds);
    }

    /**
     * Format khoảng thời gian tính theo phút.
     *
     * @param instant thời điểm cần so sánh
     * @return "x minute(s) ago"
     */
    private String formatInMinutes(Instant instant) {
        long elapseMinutes = ChronoUnit.MINUTES.between(instant, Instant.now());
        return String.format("%s minute(s) ago", elapseMinutes);
    }

    /**
     * Format khoảng thời gian tính theo giờ.
     *
     * @param instant thời điểm cần so sánh
     * @return "x hour(s) ago"
     */
    private String formatInHours(Instant instant) {
        long elapseHours = ChronoUnit.HOURS.between(instant, Instant.now());
        return String.format("%s hour(s) ago", elapseHours);
    }

    /**
     * Khi khoảng thời gian đã quá 24 giờ, format về chuỗi ngày ISO (YYYY-MM-DD).
     *
     * @param instant thời điểm cần định dạng
     * @return chuỗi ngày ISO
     */
    private String formatInDate(Instant instant) {
        LocalDateTime localDateTime =
                instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        java.time.format.DateTimeFormatter dateTimeFormatter =
                java.time.format.DateTimeFormatter.ISO_DATE;

        return localDateTime.format(dateTimeFormatter);
    }
}
