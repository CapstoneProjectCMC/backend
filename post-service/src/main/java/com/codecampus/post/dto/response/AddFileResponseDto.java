package com.codecampus.post.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AddFileResponseDto {
    private int code;
    private String message;
    private String status;
    private Result result;

    @Data
    public static class Result {
        private List<DataItem> datas;
    }

    @Data
    public static class DataItem {
        private String presignedUrl;
    }
}
